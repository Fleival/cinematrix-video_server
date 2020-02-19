package com.denspark.core.video_parser.link_parser.impls;

import com.denspark.config.CinemixServerConfiguration;
import com.denspark.core.video_parser.link_parser.LinkGrabber;
import com.denspark.core.video_parser.link_parser.LinkParser;
import com.denspark.core.video_parser.model.Link;
import com.denspark.core.video_parser.model.XLink;
import com.denspark.core.video_parser.model.XLinkType;
import com.denspark.db.service.FilmixService;
import org.springframework.context.ApplicationContext;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class FilmixLinkParser extends LinkParser {

    private FilmixService filmixService;


    private static volatile FilmixLinkParser mInstance;

    private FilmixLinkParser(String siteName, XLinkType type, int THREAD_COUNT, ApplicationContext context, CinemixServerConfiguration configuration) {
        super(siteName, type, THREAD_COUNT, configuration);
        this.filmixService = (FilmixService) context.getBean("filmixService");
    }

    public static FilmixLinkParser getInstance(String siteName, XLinkType type, int THREAD_COUNT, ApplicationContext context, CinemixServerConfiguration configuration) {
        if (mInstance == null) {
            synchronized (FilmixLinkParser.class) {
                if (mInstance == null) {
                    mInstance = new FilmixLinkParser(siteName, type, THREAD_COUNT, context, configuration);
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

    @Override protected void writeRatingToDB(Set<XLink> xLinks) {
        filmixService.saveRating(xLinks);
    }

    @Override
    public void setStartPage(String startPage) {
        super.startPage = startPage;
    }

    @Override
    protected void stopInstance() {
        mInstance = null;
    }


}
