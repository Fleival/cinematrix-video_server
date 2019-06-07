package com.denspark.core.video_parser.article_parser;

import com.denspark.CinematrixVideoConfiguration;
import com.denspark.core.video_parser.Parser;
import com.denspark.core.video_parser.model.XLink;
import com.denspark.core.video_parser.model.XLinkType;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public abstract class ArticleParser extends Parser {
    protected int splitListSize;
    private Set<XLink> xLinkSet;
    private Set<ArticleGrabber> articleGrabbers = new LinkedHashSet<>();
    private Set<ArticleGrabber> splitArticleGrabbers;
    // TODO: 02.05.2019 Generalize entity type
    protected ApplicationContext context;
    protected ConcurrentHashMap<String, Integer> genreMap = new ConcurrentHashMap<>();
    protected ConcurrentHashMap<String, Integer> personMap = new ConcurrentHashMap<>();
    protected ConcurrentHashMap<String, XLink> personXLinkMap;
    protected AtomicInteger personXLinkMapId;
    protected AtomicInteger genreId;
    protected AtomicInteger personId;

    protected ArticleParser(String siteName, XLinkType type, int THREAD_COUNT, ApplicationContext context, CinematrixVideoConfiguration configuration) {
        super(siteName, type, THREAD_COUNT, configuration);
        this.context = context;
    }

    public abstract void initDBMap();

    protected abstract List<Integer> getIdListOfExistingEntries();

    private void checkXlinks(List<Integer> ids) {
        System.out.println("Comparing " + ids.size() + " id with loaded xLink set");
        xLinkSet.forEach(
                xLink -> {
                    if (!ids.contains(xLink.getId())) {
                        xLink.setExistsInDb(false);
                    }
                }
        );
    }


    @Override
    protected void parserStarter() {
        doParse.set(true);
        splitArticleGrabbers = new LinkedHashSet<>();
        AtomicInteger count = new AtomicInteger(1);
        if (multiParserUtils.fileExist(xLinkXmlFilename)) {
            xLinkSet = multiParserUtils.readXlinks(xLinkXmlFilename);
        }
        if (multiParserUtils.fileExist(multiParserUtils.getXlinkXMLfilename(XLinkType.PERSON_LINKS, "Filmix"))) {
            personXLinkMap = multiParserUtils.readXlinksToUrlMap(multiParserUtils.getXlinkXMLfilename(XLinkType.PERSON_LINKS, "Filmix"));
            personXLinkMapId = new AtomicInteger(personXLinkMap.size());
        }
        checkXlinks(getIdListOfExistingEntries());

        Set<XLink> trueLinkSet = xLinkSet.stream().filter(
                xLink -> (!(xLink.isExistsInDb()))
        ).collect(Collectors.toSet());

        List<List<XLink>> splitBigList = splitBigListIntoSmall(trueLinkSet, splitListSize);

        for (List<XLink> list : splitBigList) {
            if (doParse.get()) {
                try {
                    Thread.sleep(PAUSE_TIME);
                    System.out.println("List#: " + count.get() + "/" + splitBigList.size());
                    grab(list);

                    if (count.get() % 10 == 0) {
                        saveResultToDB();
                        splitArticleGrabbers.clear();
                        multiParserUtils.write(xLinkXmlFilename, xLinkSet);
                    }

                    if (count.get() == splitBigList.size()) {
                        saveResultToDB();
                        multiParserUtils.write(xLinkXmlFilename, xLinkSet);
                    }

                    count.incrementAndGet();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                saveResultToDB();
                multiParserUtils.write(xLinkXmlFilename, xLinkSet);
//                stopInstance();
                stopParser();

                break;
            }
        }
        System.out.println("Grab complete");
//        stopInstance();
        stopParser();

    }

    private void grab(List<XLink> links) {
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        //Metrics
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // Асинхронно загружаем содержимое всех веб-страниц
        List<CompletableFuture<ArticleGrabber>> asyncArticleFutures = links
                .stream()
                .map(webPageXlink -> parseXlink(webPageXlink))
                .collect(Collectors.toList());

        try {
            // Создаём комбинированный Future, используя allOf()
            allFutures = CompletableFuture.allOf(
                    asyncArticleFutures.toArray(new CompletableFuture[0])
            );
            // Когда все задачи завершены, вызываем future.join(), чтобы получить результаты и собрать их в список
            CompletableFuture<List<ArticleGrabber>> allAsyncArticleGrabbers = allFutures.thenApply(v -> {
                return asyncArticleFutures.stream()
                        .map(asyncArticleFuture -> asyncArticleFuture.join())
                        .collect(Collectors.toList());
            });
            CompletableFuture<Set<ArticleGrabber>> articleGrabberSetFuture = allAsyncArticleGrabbers.thenApply(
                    asyncArticleGrabbers -> {
                        return new LinkedHashSet<>(asyncArticleGrabbers);
                    });
            splitArticleGrabbers.addAll(articleGrabberSetFuture.get());
            articleGrabbers.addAll(splitArticleGrabbers);

            articleGrabbers.forEach(
                    articleGrabber -> {
                        XLink xLink_temp = articleGrabber.getXLink();
                        xLinkSet.remove(xLink_temp);
                        xLink_temp.setExistsInDb(true);
                        xLinkSet.add(xLink_temp);
                    }
            );

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            stopParser();
        } catch (NullPointerException npe) {
            stopParser();
        }
        stopWatch.stop();
        System.out.println("Processed in "
                + stopWatch.getTime() / 1000 +
                " seconds");
        executorService.shutdown();
    }

    protected abstract CompletableFuture<ArticleGrabber> parseXlink(XLink webPageXlink);

    protected boolean shouldVisit(XLink xLink) {
        if (xLink.isExistsInDb()) {
            return false;
        }
        if (xLink.getUrl().endsWith(".pdf")) {
            return false;
        }
        return true;
    }

    public List<List<XLink>> splitBigListIntoSmall(Set<XLink> bigList, int listSize) {
        List<XLink> list = new ArrayList<>();
        list.addAll(bigList);
        List<List<XLink>> splitedLinkList = ListUtils.partition(list, listSize);
        System.out.println("List with " + list.size() + " urls splited into " + splitedLinkList.size() + " lists with " + listSize + " item every part");
        return splitedLinkList;
    }


    public void saveResultToDB() {
        switch (type) {
            case FILM_LINKS: {
                splitArticleGrabbers.forEach(
                        articleGrabber -> {
                            writeFilmToDB(articleGrabber);
                        }
                );
            }
            break;
            case PERSON_LINKS: {
                splitArticleGrabbers.forEach(
                        articleGrabber -> {
                            writePersonToDB(articleGrabber);
                        }
                );
            }
            break;
        }
    }

    protected abstract void writePersonToDB(ArticleGrabber articleGrabber);

    protected abstract void writeFilmToDB(ArticleGrabber articleGrabber);


}
