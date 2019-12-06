package com.denspark.resources;

import com.codahale.metrics.annotation.Timed;
import com.denspark.client.dto.UserDto;

import javax.inject.Singleton;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.net.URI;

@Singleton
@Path("/signup")
public class RegistrationResource {


    @POST
    @Timed
    public Response register(@FormParam("firstName") String firstName,
                             @FormParam("lastName") String lastName,
                             @FormParam("email") String email,
                             @FormParam("password") String password) {

        UserDto newUser = new UserDto();
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setPassword(password);

        return Response.seeOther(URI.create("/")).build();
    }
}