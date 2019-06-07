package com.denspark.core.video_parser.article_parser;

import com.denspark.core.video_parser.model.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.function.Supplier;

public abstract class ArticleGrabber implements Supplier<ArticleGrabber> {
    protected static final int TIMEOUT = 60000;
    protected XLinkType type;
    protected XLink xLink;
    protected SiteCss siteCss;


    public ArticleGrabber(XLinkType type, XLink xLink, SiteCss siteCss) {
        this.type = type;
        this.xLink = xLink;
        this.siteCss = siteCss;
    }

    @Override
    public ArticleGrabber get() {
        Document document;
        try {
            Connection targetConnection = Jsoup.connect(xLink.getUrl());
            targetConnection.timeout(TIMEOUT);

            document = targetConnection.get();

            processItem(type, document);
        } catch (IOException e) {
            throw new RuntimeException("IOException: " + e.getMessage() + " " + xLink.getUrl());
        } catch (NullPointerException e) {
            System.out.println("NPE here");
            e.printStackTrace();
        }
        return this;
    }

    protected void processItem(XLinkType type, Document document) {
        switch (type) {
            case FILM_LINKS: {
                parseFilm(document);
            }
            break;
            case PERSON_LINKS: {
                parsePerson(document, xLink);
            }
            break;
        }
    }

    protected abstract void parsePerson(Document document, XLink xLink);

    protected abstract void parseFilm(Document document);

    public XLink getXLink() {
        return xLink;
    }

    public abstract Film getFilm();

    public abstract Person getPerson();


}
