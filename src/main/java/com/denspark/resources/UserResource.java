package com.denspark.resources;

import com.denspark.config.CinematrixServerConfiguration;
import com.denspark.db.service.CinematrixService;
import com.denspark.model.user.User;
import com.denspark.utils.mail.SendEmail;
import com.denspark.views.UserValidationView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/user")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class UserResource {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final ApplicationContext context;
    private CinematrixService cinematrixService;
    private final CinematrixServerConfiguration configuration;


    private ApplicationEventPublisher eventPublisher;


    public UserResource(ApplicationContext context, CinematrixServerConfiguration configuration) {
        this.context = context;
        this.cinematrixService = (CinematrixService) context.getBean("cinematrixService");
        this.configuration = configuration;
        this.eventPublisher = context;

    }

    @GET
    @Path("/info")
    @Produces(MediaType.TEXT_HTML)
    public UserValidationView getPersonViewMustache() {
        User user = new User();
//        user.setId(1);
        user.setFirstName("Peter");
//        user.setSecondName("Pen");
        user.setGender("male");
        user.setEmail("StreletsDY@gmail.com");

        // inside your getSalesUserData() method
        ExecutorService emailExecutor = Executors.newSingleThreadExecutor();
        emailExecutor.execute(new Runnable() {
            @Override
            public void run() {

                SendEmail confirmationMailSender = new SendEmail(user);
                confirmationMailSender.sendConfirmationMail();
                cinematrixService.create(user);
            }
        });

        emailExecutor.shutdown(); // it is very important to shutdown your non-singleton ExecutorService.

        return new UserValidationView(UserValidationView.Template.MUSTACHE, user);
    }

}