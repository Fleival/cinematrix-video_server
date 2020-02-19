package com.denspark.resources;

import com.denspark.client.dto.UserDto;
import com.denspark.client.registration.OnRegistrationCompleteEvent;
import com.denspark.db.service.UserService;
import com.denspark.model.user.User;
import io.dropwizard.jersey.params.DateTimeParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Locale;
import java.util.Optional;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;


@Controller
@Path("/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class RegistrationController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final ApplicationContext context;


    private UserService userService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private Model model;


    private ApplicationEventPublisher eventPublisher;


    public RegistrationController(ApplicationContext context) {
        this.context = context;
        userService = context.getBean(UserService.class);
        eventPublisher = context;
    }


    // Registration

    @POST
    @Path("/user/registration")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response registerUserAccount(@FormParam("firstName") String firstName,
                                        @FormParam("lastName") String lastName,
                                        @FormParam("email") String email,
                                        @FormParam("gender") String gender,
                                        @FormParam("city") String city,
                                        @FormParam("country") String country,
                                        @FormParam("password") String password,
                                        @Context HttpServletRequest request) {

        UserDto accountDto = new UserDto();

        accountDto.setFirstName(firstName);
        accountDto.setLastName(lastName);
        accountDto.setEmail(email);
        accountDto.setGender(gender);
        accountDto.setCity(city);
        accountDto.setCountry(country);
        accountDto.setUsing2FA(false);
        accountDto.setPassword(password);
        LOGGER.info("Registering user account with information: {}", accountDto);

        final User registered = userService.registerNewUserAccount(accountDto);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, new Locale("en", "US"), getAppUrl(request)));
        return Response.seeOther(URI.create(request.getContextPath())).build();
    }

    @GET
    @Path("/registrationConfirm")
    public Response confirmRegistration(@Context final HttpServletRequest request, @QueryParam("token") final String token) throws UnsupportedEncodingException {
        Locale locale = new Locale("en", "US");
        final String result = userService.validateVerificationToken(token);
        if (result.equals("valid")) {
            final User user = userService.getUser(token);
            // if (user.isUsing2FA()) {
//             model.addAttribute("qr", userService.generateQRUrl(user));
//             return "redirect:/qrcode.html?lang=" + locale.getLanguage();
            // }

//            authWithoutPassword(user);
            model.addAttribute("message", messages.getMessage("message.accountVerified", null, locale));
            return Response.seeOther(URI.create(request.getContextPath())).build();
        }

//        model.addAttribute("message", messages.getMessage("auth.message." + result, null, locale));
//        model.addAttribute("expired", "expired".equals(result));
//        model.addAttribute("token", token);
        return Response.seeOther(URI.create("bedUser")).build();
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