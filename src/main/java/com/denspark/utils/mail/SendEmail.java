package com.denspark.utils.mail;

import com.denspark.model.user.User;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendEmail {
    private User user;
    private Session session;
    private String SMTP_SERVER = "sparkbrains.pp.ua";
    private String USERNAME = "admin@sparkbrains.pp.ua";
    private String PASSWORD = "d26011986";

    private String EMAIL_FROM = "admin@sparkbrains.pp.ua";
    private String EMAIL_TO = "streletsdy@gmail.com";
    private String EMAIL_TO_CC = "";

    private String EMAIL_SUBJECT = "Registration at Cinematrix Server";
    private String EMAIL_TEXT = "We send you confirmation link ";

    private SendEmail() {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_SERVER);
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.auth", "true");
        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USERNAME, PASSWORD);
                    }
                });

    }

    public SendEmail(User user) {
        this();
        this.user = user;
    }

    public void sendConfirmationMail() {
        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(user.getEmail()));
            message.setSubject(EMAIL_SUBJECT);
            message.setText(EMAIL_TEXT);

            Transport.send(message);

            System.out.println("Done");


        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
