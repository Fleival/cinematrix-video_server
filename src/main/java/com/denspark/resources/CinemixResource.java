package com.denspark.resources;

import com.denspark.core.video_parser.video_models.cinema.Film;
import com.denspark.db.service.FilmixService;
import com.denspark.db.service.UserService;
import com.denspark.model.user.User;
import com.denspark.web.views.IndexView;
import com.denspark.web.views.LoginView;
import com.denspark.web.views.RegisterView;
import io.dropwizard.views.View;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.knowm.sundial.SundialJobScheduler;
import org.quartz.triggers.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Controller
@Singleton
@Path("/")
public class CinemixResource {
    private static final Logger logger = LoggerFactory.getLogger(CinemixResource.class);
    private final ApplicationContext context;
    private FilmixService filmixService;

    private UserService userService;
    private final Model model;

    public CinemixResource(ApplicationContext context, Model model) {
        this.context = context;
        this.filmixService = (FilmixService) context.getBean("filmixService");
        this.userService = context.getBean(UserService.class);
        this.model = model;
    }


    @GET
    @Produces(MediaType.TEXT_HTML)
    public View index(@QueryParam("status") String s1) {
        long totalFilmCount = filmixService.countAllMovies();
        long yesterdayFilmsCount = filmixService.countYesterdayMovies();
        long lastUpdFilmsCount = filmixService.countLastUpdMovies();
        boolean isUserAuthenticated = false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (final GrantedAuthority grantedAuthority : authorities) {
            if (!(grantedAuthority.getAuthority().equals("ROLE_ANONYMOUS"))) {
                isUserAuthenticated = true;
            }
        }
        model.addAttribute("isUserAuthenticated", isUserAuthenticated);
        if (isUserAuthenticated) {
            model.addAttribute("userName", ((User) authentication.getPrincipal()).getFirstName());
        } else {
            model.addAttribute("userName", "guest");
        }
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

            Duration duration = new Duration(previousFireTime, now);
            elapsed = String.valueOf(duration.getStandardMinutes());
            DateTime wtfTime = new DateTime(myTrigger.getFireTimeAfter(new Date(System.currentTimeMillis())));

            logger.info("My Trigger StartTime: " + dtf.print(fireTime));
            logger.info("My Trigger NextFireTime: " + dtf.print(nextFireTime));
            logger.info("My Trigger PreviousFireTime: " + dtf.print(previousFireTime));
            logger.info("My Trigger wtfTime: " + dtf.print(wtfTime));
            logger.info("Elapsed after PreviousFireTime: " + elapsed);
        }
        model.addAttribute("status", s1);
        return new IndexView(totalFilmCount, yesterdayFilmsCount, lastUpdFilmsCount, isUpdating, elapsed, model);
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

    @GET
    @Path("/get_user")
//    @Secured("ROLE_USER")
//    @PreAuthorize("hasRole('ROLE_USER')")
    @Produces(APPLICATION_JSON)
    public Response getUser() {
        Authentication userAuth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) userAuth.getPrincipal();

        return Response.ok(user).build();
    }

    @POST
    @Path("/update_user_favorite_movie")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void addMovieToLoggedUser(@FormParam("film_id") Integer filmId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Film f = filmixService.findFilmById(filmId);
        userService.addUserFavoriteFilmAndUpdate(user, f);
    }

    @POST
    @Path("/update_user_favorite_tv_series")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void addTvSeriesToLoggedUser(@FormParam("tv_series_id") Integer tvSeriesId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Film f = filmixService.findFilmById(tvSeriesId);
        userService.addUserFavoriteTvSeriesAndUpdate(user, f);
    }

}