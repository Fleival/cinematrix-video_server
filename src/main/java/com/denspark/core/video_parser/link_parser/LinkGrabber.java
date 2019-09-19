package com.denspark.core.video_parser.link_parser;

import com.denspark.core.video_parser.model.Link;
import com.denspark.core.video_parser.model.SiteCss;
import com.denspark.core.video_parser.model.XLink;
import com.denspark.core.video_parser.model.XLinkType;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

public abstract class LinkGrabber implements Supplier<LinkGrabber> {
    protected static final int TIMEOUT = 60000;
    private XLinkType type;
    protected Link link;
    protected URL url;
    protected Set<XLink> xLinkSet_ItemLinks = new LinkedHashSet<>();
    protected SiteCss siteCss;

    protected LinkGrabber(Link link, SiteCss siteCss, XLinkType type) {
        this.siteCss = siteCss;
        this.type = type;
        this.link = link;
    }

    @Override
    public LinkGrabber get() {
        Document document;
        try {
            Connection targetConnection = Jsoup.connect(link.getUrl());
            targetConnection.timeout(TIMEOUT);

            document = targetConnection.get();
            processItem(type, document);


            link.setProcessed(true);
        } catch (IOException e) {
            e.printStackTrace();
            link.setError(true);
        } catch (NullPointerException e) {
            System.out.println("NPE here");
            e.printStackTrace();
            link.setError(true);
        }
        return this;
    }

    private void processItem(XLinkType type, Document document) {
        switch (type) {
            case PERSON_LINKS: {
                findPersonLinks(document);
            }
            break;
            case FILM_LINKS: {
                findFilmLinks(document);
            }
            break;
        }
    }

    protected abstract void findPersonLinks(Document document);

    protected abstract void findFilmLinks(Document document);

    protected XLink createXlink(Element element) {
        XLink xLink = new XLink();
        switch (type) {
            case PERSON_LINKS: {
                String id = element.selectFirst("a > span > span").attr("data-id");
                xLink.setId(Integer.parseInt(id));
                xLink.setType(type.toString());
                xLink.setUrl(element.attr("href"));
                xLink.setUpdateDate(null);
                xLink.setExistsInDb(false);
                xLink.setNeedToUpdateRecord(true);
            }
            break;
            case FILM_LINKS: {
                String url = element.selectFirst("div.short > a").attr("href");
                String id = element.attr("data-id");
                String dateS = element.selectFirst("div.full > div.top-date > div.block-date > time").attr("datetime");

                String likes = element.selectFirst("span.hand-up.icon-like > span").text();
                String dislikes = element.selectFirst("span.hand-down.icon-dislike > span").text();


                xLink.setType(type.toString());
                xLink.setUrl(url);
                xLink.setUpdateDate(dateS);
                xLink.setId(Integer.parseInt(id));
                xLink.setExistsInDb(false);
                xLink.setNeedToUpdateRecord(true);
                xLink.setPosRating(Integer.parseInt(likes));
                xLink.setNegRating(Integer.parseInt(dislikes));

//                System.out.println(xLink.getUrl() + xLink.getId() + xLink.getUpdateDateObj() + likes + dislikes);

            }
            break;
        }
        return xLink;
    }

    public Link getLink() {
        return link;
    }

    public Set<XLink> getxLinkSet_ItemLinks() {
        return xLinkSet_ItemLinks;
    }
}
