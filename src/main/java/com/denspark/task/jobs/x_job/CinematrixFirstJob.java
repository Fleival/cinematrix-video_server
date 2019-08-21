package com.denspark.task.jobs.x_job;

import org.knowm.sundial.Job;
import org.knowm.sundial.exceptions.JobInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CinematrixFirstJob extends Job {
    private static final Logger logger = LoggerFactory.getLogger(CinematrixFirstJob.class);

    @Override
    public void doRun() throws JobInterruptException {
        logger.info("This job is just printing message!");
    }

}
