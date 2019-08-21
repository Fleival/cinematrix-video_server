package com.denspark.task.jobs.person;

import com.denspark.config.CinematrixVideoConfiguration;
import com.denspark.core.video_parser.Parser;
import com.denspark.core.video_parser.ParserFactory;
import com.denspark.core.video_parser.article_parser.ArticleParser;
import com.denspark.core.video_parser.link_parser.LinkParser;
import com.denspark.core.video_parser.model.XLinkType;
import com.denspark.task.jobs.movie.InitializeConfigAndUpdateMovieLinks;
import org.knowm.sundial.Job;
import org.knowm.sundial.SundialJobScheduler;
import org.knowm.sundial.annotations.SimpleTrigger;
import org.knowm.sundial.exceptions.JobInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.TimeUnit;

@SimpleTrigger(repeatCount = 1, repeatInterval = 4, timeUnit = TimeUnit.HOURS ,jobDataMap = {"LINK_THREADS:16", "PERSON_THREADS:24", "X_TYPE:PERSON_LINKS","SPLIT_LIST_SIZE:500"})
public class InitializeConfigAndUpdatePersonLinks extends Job {
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
                configuration);

        logger.info("updateConfig started");
        parser.updateConfig();
        parser.stopParser();
        logger.info("parser stopped");
        logger.info("updateConfig finished");

        String siteName = "Filmix";

        int threads = Integer.parseInt(getJobContext().get("LINK_THREADS"));
        logger.info("LINK_THREADS = " + threads);

        LinkParser linkParser = ParserFactory.getInstance().getFilmixLinkParser(
                siteName,
                type,
                threads,
                configuration);
        logger.info("LinkParser started");
        linkParser.setStartPage(linkParser.getSiteCss().getPersonsSectionUrl());
        linkParser.setLastPage(linkParser.getSiteCss().getPersonListLastPageIndex());
        logger.info("LinkParser started");
        linkParser.startParser();
        linkParser.stopParser();

        threads = Integer.parseInt(getJobContext().get("PERSON_THREADS"));
        logger.info("LINK_THREADS = " + threads);

        int splitListSize = Integer.parseInt(getJobContext().get("SPLIT_LIST_SIZE"));

        ArticleParser personParser = ParserFactory.getInstance().getFilmixArticleParser(
                siteName,
                type,
                threads,
                context,
                splitListSize,
                configuration);
        logger.info("PersonParser started");
        personParser.startParser();
        personParser.stopParser();
        logger.info("Update job finished");
    }
}
