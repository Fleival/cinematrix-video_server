package com.denspark.utils.mail;

import com.denspark.client.registration.OnRegistrationCompleteEvent;
import com.denspark.config.CinemixServerConfiguration;
import com.denspark.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JamesMailSender implements Runnable {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static JamesMailSender instance;
    private ExecutorService emailExecutor;
    private Session session;
    private CinemixServerConfiguration configuration;

    private Message message;

    private JamesMailSender() {
    }

    private JamesMailSender(CinemixServerConfiguration configuration) {
        Properties props = new Properties();
        this.configuration = configuration;
        props.put("mail.smtp.host", configuration.getSmtp_host());
        props.put("mail.smtp.port", configuration.getSmtp_port());
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.auth", "true");
        this.emailExecutor = Executors.newSingleThreadExecutor();
        this.session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(configuration.getMail_username(), configuration.getMail_password());
                    }
                });

    }

    public static synchronized JamesMailSender getInstance(CinemixServerConfiguration configuration) {
        if (instance == null) {
            instance = new JamesMailSender(configuration);
        }
        return instance;
    }

    @Override
    public void run() {
        try {

            Transport.send(message);

            LOGGER.debug("message sent");


        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMail(final OnRegistrationCompleteEvent event, final User user, final String token, final MessageSource messages) {
        this.message = constructConfirmationMail(event, user, token, messages);

        emailExecutor.execute(this);
    }

    public void sendMail(SimpleMailMessage notification) {
        this.message = constructConfirmationMail(notification);

        emailExecutor.execute(this);
    }


    private Message constructConfirmationMail(final OnRegistrationCompleteEvent event, final User user, final String token, final MessageSource messages) {
        try {
            final String recipientAddress = user.getEmail();
            final String subject = "Registration at CINEMIX Server.Please confirm  your account";
            final String confirmationUrl = event.getAppUrl() + "/registrationConfirm?token=" + token;
            final String message = messages.getMessage("message.regSucc", null, event.getLocale());
            Message email = new MimeMessage(session);
            email.setFrom(new InternetAddress(configuration.getMail_email_from()));
            email.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipientAddress));
            email.setSubject(subject);
            email.setText(message + " \r\n" + confirmationUrl);
            return email;

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private Message constructConfirmationMail(SimpleMailMessage notification) {
        try {
            final String recipientAddress = Objects.requireNonNull(notification.getTo())[0];
            final String subject = notification.getSubject();
            final String message = notification.getText();
            Message email = new MimeMessage(session);
            email.setFrom(new InternetAddress(configuration.getMail_email_from()));
            email.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipientAddress));
            email.setSubject(subject);
            email.setText(message);
            return email;

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}