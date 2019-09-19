package com.denspark.task.jobs.movie;

import com.denspark.config.CinematrixVideoConfiguration;
import com.denspark.core.video_parser.Parser;
import com.denspark.core.video_parser.ParserFactory;
import com.denspark.core.video_parser.article_parser.ArticleParser;
import com.denspark.core.video_parser.link_parser.LinkParser;
import com.denspark.core.video_parser.model.XLinkType;
import org.knowm.sundial.Job;
import org.knowm.sundial.SundialJobScheduler;
import org.knowm.sundial.annotations.SimpleTrigger;
import org.knowm.sundial.exceptions.JobInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.TimeUnit;

@SimpleTrigger(repeatInterval = 15, timeUnit = TimeUnit.MINUTES, jobDataMap = {"LINK_THREADS:16", "MOVIES_THREADS:24", "X_TYPE:FILM_LINKS", "SPLIT_LIST_SIZE:500"})
public class InitializeConfigAndUpdateMovieLinks extends Job {
    private static final Logger logger = LoggerFactory.getLogger(InitializeConfigAndUpdateMovieLinks.class);

    @Override public void doRun() throws JobInterruptException {
        CinematrixVideoConfiguration configuration =
                (CinematrixVideoConfiguration) SundialJobScheduler.getServletContext()
                        .getAttribute("configuration");
        XLinkType type = XLinkType.fromString(getJobContext().get("X_TYPE"));
        ApplicationContext context = (ApplicationContext) SundialJobScheduler.getServletContext().getAttribute("context");
        logger.info("X_TYPE = " + type);
        Parser parser = ParserFactory.getInstance().getFilmixParser(
                "Filmix",
                type,
                1,
                context,
                configuration);

        logger.info("updateConfig started");
        parser.updateConfig();
        logger.info("updateConfig finished");
        parser.stopParser();
        logger.info("parser stopped " + parser);

        String siteName = "Filmix";

        Integer threads = Integer.parseInt(getJobContext().get("LINK_THREADS"));
        logger.info("LINK_THREADS = " + threads);

        LinkParser linkParser = ParserFactory.getInstance().getFilmixLinkParser(
                siteName,
                type,
                threads,
                context,
                configuration);
        logger.info("LinkParser started");
        linkParser.setStartPage(linkParser.getSiteCss().getUrl());
        linkParser.setLastPage(linkParser.getSiteCss().getArticleListLastPageIndex());
        logger.info("LinkParser params initialized");
        linkParser.startParser();
        logger.info("Links to parse obtained");
        linkParser.stopParser();

        threads = Integer.parseInt(getJobContext().get("MOVIES_THREADS"));
        logger.info("LINK_THREADS = " + threads);

        Integer splitListSize = Integer.parseInt(getJobContext().get("SPLIT_LIST_SIZE"));

        ArticleParser moviesParser = ParserFactory.getInstance().getFilmixArticleParser(
                siteName,
                type,
                threads,
                context,
                splitListSize,
                configuration);

        logger.info("MoviesParser started");
        moviesParser.startParser();
        logger.info("MoviesParser finished");
        moviesParser.stopParser();
        logger.info("Job is DONE");
    }
}
