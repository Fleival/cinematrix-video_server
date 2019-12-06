package com.denspark.utils.mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendEmailSMTP {

    private static final String SMTP_SERVER = "sparkbrains.pp.ua";
    private static final String USERNAME = "admin@sparkbrains.pp.ua";
    private static final String PASSWORD = "d26011986";

    private static final String EMAIL_FROM = "admin@sparkbrains.pp.ua";
    private static final String EMAIL_TO = "streletsdy@gmail.com";
    private static final String EMAIL_TO_CC = "";

    private static final String EMAIL_SUBJECT = "Test Send Email via SMTP";
    private static final String EMAIL_TEXT = "Hello Java Mail \n ABC123";

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_SERVER);
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USERNAME, PASSWORD);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(EMAIL_TO));
            message.setSubject(EMAIL_SUBJECT);
            message.setText(EMAIL_TEXT);

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }
}