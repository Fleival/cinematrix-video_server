package com.denspark.core.video_parser.link_parser;

import com.denspark.CinematrixVideoConfiguration;
import com.denspark.core.video_parser.model.Link;
import com.denspark.core.video_parser.model.SiteCss;
import com.denspark.core.video_parser.model.XLink;
import com.denspark.core.video_parser.model.XLinkType;
import com.denspark.core.video_parser.Parser;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public abstract class LinkParser extends Parser {
    private int lastPage;
    private URL startUrl;


    private Set<XLink> xLinkSet = new LinkedHashSet<>();
    private Set<Link> linkSet = new LinkedHashSet<>();

    public LinkParser(String siteName, XLinkType type, int THREAD_COUNT, CinematrixVideoConfiguration configuration) {
        super(siteName, type, THREAD_COUNT, configuration);
    }

    @Override
    protected void parserStarter() {
        initLinkSet(startPage);
        doParse.set(true);

        AtomicInteger count = new AtomicInteger(1);
        if (multiParserUtils.fileExist(xLinkXmlFilename)) {
            xLinkSet = multiParserUtils.readXlinks(xLinkXmlFilename);
        }

//        Set<Link> trueLinkSet = linkSet.stream().filter(
//                link -> !link.isProcessed()).collect(Collectors.toSet());
        Set<Link> trueLinkSet =linkSet;

        System.out.println(trueLinkSet.size());
        List<List<Link>> splitBigList = splitBigListIntoSmall(trueLinkSet, 200);

        for (List<Link> list : splitBigList) {

            if (doParse.get()) {
                try {
                    Thread.sleep(PAUSE_TIME);

                    System.out.println("List#: " + count.getAndIncrement() + "/" + splitBigList.size());
                    grab(list);

                    multiParserUtils.write(xLinkXmlFilename, xLinkSet);

                    writeOrUpdateLinks();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                writeOrUpdateLinks();
                break;
            }
        }

        multiParserUtils.recountXlinksId(xLinkXmlFilename);
        stopInstance();
    }

    private void grab(List<Link> links) {
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        //Metrics
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // Асинхронно загружаем содержимое всех веб-страниц
        List<CompletableFuture<LinkGrabber>> asyncLinkFutures = links.stream()
                .map(webPageLink -> parseLink(webPageLink))
                .collect(Collectors.toList());

        try {
            // Создаём комбинированный Future, используя allOf()
            allFutures = CompletableFuture.allOf(
                    asyncLinkFutures.toArray(new CompletableFuture[0])
            );
            // Когда все задачи завершены, вызываем future.join(), чтобы получить результаты и собрать их в список
            CompletableFuture<List<LinkGrabber>> allAsyncLinkGrabbers = allFutures.thenApply(v -> {
                return asyncLinkFutures.stream()
                        .map(asyncLinkFuture -> asyncLinkFuture.join())
                        .collect(Collectors.toList());
            });

            CompletableFuture<Set<XLink>> xLinkSetFuture = allAsyncLinkGrabbers.thenApply(
                    asyncLinkGrabbers -> {
                        return asyncLinkGrabbers.stream()
                                .map(asyncLinkGrabber -> asyncLinkGrabber.getxLinkSet_ItemLinks())
                                .flatMap(xLinks -> xLinks.stream())
                                .collect(Collectors.toSet());
                    });
            CompletableFuture<Set<Link>> linksFuture = allAsyncLinkGrabbers.thenApply(
                    asyncItemLinkGrabbers -> {
                        return asyncItemLinkGrabbers.stream()
                                .map(asyncLinkGrabber -> asyncLinkGrabber.getLink())
                                .collect(Collectors.toSet());
                    });

            xLinkSet.addAll(xLinkSetFuture.get());
            linkSet.addAll(linksFuture.get());

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (NullPointerException npe) {
        }
        stopWatch.stop();
        System.out.println("Processed in "
                + stopWatch.getTime() / 1000 +
                " seconds");
        executorService.shutdown();
    }

    protected abstract CompletableFuture<LinkGrabber> parseLink(Link link);

    private void initLinkSet(String startPageUrl) {
        //StartURL
        try {
            startUrl = new URL(startPageUrl);
            urlBase = startUrl.toString().replaceAll("(.*//.*/).*", "$1");
            Set<URL> urls = new LinkedHashSet<>();
            urls.add(startUrl);
            urls.addAll(nextPageUrlSetGenerator(lastPage));

            if (multiParserUtils.fileExist(linksXmlFilename)) {
                linkSet = multiParserUtils.readLinks(linksXmlFilename);
            }

            urls.forEach(
                    url -> {
                        Link link = new Link(url.toString(), false, false);
                        linkSet.add(link);
                    }
            );

            writeOrUpdateLinks();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    protected boolean shouldVisit(Link link) {
//        if (link.isProcessed()) {
//            return false;
//        }
        if (!link.getUrl().startsWith(urlBase)) {
            return false;
        }
        if (link.getUrl().endsWith(".pdf")) {
            return false;
        }
//        if (link.isError()) {
//            return false;
//        }
        return true;
    }

    private Set<URL> nextPageUrlSetGenerator(int lastPage) {
        Set<URL> newUrlSet = new LinkedHashSet<>();
        for (int i = 2; i <= lastPage; i++) {
            try {
                String nextUrl_S = startUrl
                        + siteCss.getSeparator()
                        + siteCss.getPageQuerySeparator()
                        + siteCss.getSeparator()
                        + i
                        + siteCss.getSeparator();
                //URL EXAMPLE: http://filmix.cc/films+/page/2/ http://filmix.cc/page/2/
                URL nextUrl = new URL(nextUrl_S);
                newUrlSet.add(nextUrl);
            } catch (MalformedURLException e) { // ignore bad urls
            }
        }
        return newUrlSet;
    }

    public List<List<Link>> splitBigListIntoSmall(Set<Link> bigList, int listSize) {
        List<Link> list = new ArrayList<>(bigList);
        List<List<Link>> splitedLinkList = ListUtils.partition(list, listSize);
        System.out.println("List with " + list.size() + " urls splited into " + splitedLinkList.size() + " lists with " + listSize + " item every part");
        return splitedLinkList;
    }

    public SiteCss getSiteCss() {
        return siteCss;
    }

    public void writeOrUpdateLinks() {
        multiParserUtils.writeLinks(linksXmlFilename, linkSet);
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

}
