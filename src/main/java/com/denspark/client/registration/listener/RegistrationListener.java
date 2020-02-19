package com.denspark.client.registration.listener;

import com.denspark.client.registration.OnRegistrationCompleteEvent;
import com.denspark.config.CinemixServerConfiguration;
import com.denspark.db.service.UserService;
import com.denspark.model.user.User;
import com.denspark.utils.mail.JamesMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    private final UserService service;

    private final MessageSource messages;

    @Autowired
    private CinemixServerConfiguration serverConfiguration;

    public RegistrationListener(UserService service, MessageSource messages) {
        this.service = service;
        this.messages = messages;
    }

    // API

    @Override
    public void onApplicationEvent(final OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }


    private void confirmRegistration(final OnRegistrationCompleteEvent event) {
        final User user = event.getUser();
        final String token = UUID.randomUUID().toString();
        service.createVerificationTokenForUser(user, token);

        JamesMailSender sender = JamesMailSender.getInstance(serverConfiguration);
        sender.sendMail(event, user, token, messages);
    }
}