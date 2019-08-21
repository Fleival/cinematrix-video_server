package com.denspark.task.jobs.debug;

import com.denspark.config.CinematrixVideoConfiguration;
import com.denspark.core.video_parser.Parser;
import com.denspark.core.video_parser.ParserFactory;
import com.denspark.core.video_parser.model.XLinkType;
import org.knowm.sundial.Job;
import org.knowm.sundial.SundialJobScheduler;
import org.knowm.sundial.annotations.SimpleTrigger;
import org.knowm.sundial.exceptions.JobInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.TimeUnit;


@SimpleTrigger(repeatInterval = 1, timeUnit = TimeUnit.MINUTES, jobDataMap = {"LINK_THREADS:16", "MOVIES_THREADS:24", "X_TYPE:FILM_LINKS", "SPLIT_LIST_SIZE:500"})
public class DebugConfigWriterJob extends Job {
    private static final Logger logger = LoggerFactory.getLogger(com.denspark.task.jobs.movie.InitializeConfigAndUpdateMovieLinks.class);

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

    }
}