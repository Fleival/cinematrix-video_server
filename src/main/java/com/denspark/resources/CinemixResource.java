package com.denspark.resources;

import com.denspark.db.service.FilmixService;
import com.denspark.views.IndexView;
import com.denspark.views.LoginView;
import com.denspark.views.RegisterView;
import io.dropwizard.views.View;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.knowm.sundial.SundialJobScheduler;
import org.quartz.triggers.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Singleton
@Path("/")
public class CinemixResource {
    private static final Logger logger = LoggerFactory.getLogger(CinemixResource.class);
    private final ApplicationContext context;
    private FilmixService filmixService;

    public CinemixResource(ApplicationContext context) {
        this.context = context;
        this.filmixService = (FilmixService) context.getBean("filmixService");
    }


    @GET
    @Produces(MediaType.TEXT_HTML)
    public View index() {
        long totalFilmCount = filmixService.countAllMovies();
        long yesterdayFilmsCount = filmixService.countYesterdayMovies();
        long lastUpdFilmsCount = filmixService.countLastUpdMovies();
        boolean isUpdating = false;
        String elapsed = "";
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        List<String> jobs = SundialJobScheduler.getAllJobNames();
        Map<String, List<Trigger>> trigMap = SundialJobScheduler.getAllJobsAndTriggers();
        if (!(jobs.isEmpty())) {
            String myJob = jobs.get(0);
            Trigger myTrigger = trigMap.get(myJob).get(0);
            isUpdating = SundialJobScheduler.isJobRunning(myJob);
            logger.info("Current Job: " + myJob);
            logger.info("Is Current Job Running: " + isUpdating);
            logger.info("Is Current Trigger: " + myTrigger.getName());

            DateTime fireTime = new DateTime(myTrigger.getStartTime());
            DateTime nextFireTime = new DateTime(myTrigger.getNextFireTime());
            DateTime previousFireTime = new DateTime(myTrigger.getPreviousFireTime());
            DateTime now = new DateTime();

            Period period = new Period(previousFireTime, now);
            Duration duration = new Duration(previousFireTime, now);
            PeriodFormatter formatter = new PeriodFormatterBuilder()
//                    .appendSeconds().appendSuffix(" seconds ago\n")
                    .appendMinutes()
//                    .appendSuffix(" minutes ago\n")
//                    .appendHours().appendSuffix(" hours ago\n")
//                    .appendDays().appendSuffix(" days ago\n")
//                    .appendWeeks().appendSuffix(" weeks ago\n")
//                    .appendMonths().appendSuffix(" months ago\n")
//                    .appendYears().appendSuffix(" years ago\n")
//                    .printZeroNever()
                    .toFormatter();

//            elapsed = formatter.print(period);
            elapsed = String.valueOf(duration.getStandardMinutes());
            DateTime wtfTime = new DateTime(myTrigger.getFireTimeAfter(new Date(System.currentTimeMillis())));

            logger.info("My Trigger StartTime: " + dtf.print(fireTime));
            logger.info("My Trigger NextFireTime: " + dtf.print(nextFireTime));
            logger.info("My Trigger PreviousFireTime: " + dtf.print(previousFireTime));
            logger.info("My Trigger wtfTime: " + dtf.print(wtfTime));
            logger.info("Elapsed after PreviousFireTime: " + elapsed);
        }


        return new IndexView(totalFilmCount, yesterdayFilmsCount, lastUpdFilmsCount, isUpdating, elapsed);
    }

    @GET
    @Path("/login")
    @Produces(MediaType.TEXT_HTML)
    public View login() {
        return new LoginView();
    }

    @GET
    @Path("/registration")

    public View register() {
        return new RegisterView();
    }

}