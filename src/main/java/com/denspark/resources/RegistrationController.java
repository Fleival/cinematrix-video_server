package com.denspark.resources;

import com.denspark.client.dto.UserDto;
import com.denspark.client.registration.OnRegistrationCompleteEvent;
import com.denspark.config.CinematrixServerConfiguration;
import com.denspark.db.service.CinematrixService;
import com.denspark.db.service.IUserService;
import com.denspark.model.user.User;
import io.dropwizard.jersey.params.DateTimeParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;


@Path("/user")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class RegistrationController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final ApplicationContext context;
    private CinematrixService cinematrixService;
    private final CinematrixServerConfiguration configuration;


    @Autowired
    private IUserService userService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private ApplicationEventPublisher eventPublisher;


    public RegistrationController(ApplicationContext context, CinematrixServerConfiguration configuration) {
        this.context = context;
        this.cinematrixService = (CinematrixService) context.getBean("cinematrixService");
        this.configuration = configuration;
    }

    // Registration

    @POST
    @Path("/registration")
    @Produces(MediaType.TEXT_HTML)
    public String registerUserAccount(@Valid final UserDto accountDto, @Context HttpServletRequest request) {
        LOGGER.debug("Registering user account with information: {}", accountDto);

        final User registered = userService.registerNewUserAccount(accountDto);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
        return "success";
    }

    @GET
    @Path("/registration_t")
    @Produces(MediaType.TEXT_HTML)
    public String registerUserAccount_t(@Context HttpServletRequest httpRequest) {
        LOGGER.debug("Registering user account with information: ");

        return "success" + getAppUrl(httpRequest);
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    @GET
    @Path("/date")
    @Produces(MediaType.TEXT_PLAIN)
    public String receiveDate(@QueryParam("date") Optional<DateTimeParam> dateTimeParam) {
        if (dateTimeParam.isPresent()) {
            final DateTimeParam actualDateTimeParam = dateTimeParam.get();
            LOGGER.info("Received a date: {}", actualDateTimeParam);
            return actualDateTimeParam.get().toString();
        } else {
            LOGGER.warn("No received date");
            return null;
        }
    }

}