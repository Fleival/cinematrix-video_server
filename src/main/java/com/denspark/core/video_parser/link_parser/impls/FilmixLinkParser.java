package com.denspark.core.video_parser.link_parser.impls;

import com.denspark.CinematrixVideoConfiguration;
import com.denspark.core.video_parser.model.Link;
import com.denspark.core.video_parser.model.XLinkType;
import com.denspark.core.video_parser.link_parser.LinkGrabber;
import com.denspark.core.video_parser.link_parser.LinkParser;

import java.util.concurrent.CompletableFuture;

public class FilmixLinkParser extends LinkParser {


    private static volatile FilmixLinkParser mInstance;

    private FilmixLinkParser(String siteName, XLinkType type, int THREAD_COUNT, CinematrixVideoConfiguration configuration) {
        super(siteName, type, THREAD_COUNT, configuration);
    }

    public static FilmixLinkParser getInstance(String siteName, XLinkType type, int THREAD_COUNT, CinematrixVideoConfiguration configuration) {
        if (mInstance == null) {
            synchronized (FilmixLinkParser.class) {
                if (mInstance == null) {
                    mInstance = new FilmixLinkParser(siteName, type, THREAD_COUNT,configuration);
                }
            }
        }
        return mInstance;
    }

    @Override
    protected CompletableFuture<LinkGrabber> parseLink(Link link) {
        if (shouldVisit(link)) {
            LinkGrabber linkGrabber = new FilmixLinkGrabber(
                    link,
                    siteCss,
                    type);

            return CompletableFuture.supplyAsync(linkGrabber, executorService);
        }
        return null;
    }

    @Override
    public void setStartPage(String startPage) {
        super.startPage = startPage;
    }

    @Override
    protected void stopInstance() {
        mInstance=null;
    }
}
