package com.smartparcel.locker.service;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class EmailSender {
    private static final String USERNAME = "smartlockerparcelsystem123@gmail.com";
    private static final String APP_PASSWORD = "pdvspqvyyjsxioxc";

    public static void send(String to, String subject, String text) {
        try {
            Email email = new SimpleEmail();
            email.setHostName("smtp.gmail.com");
            email.setSmtpPort(587);
            email.setStartTLSEnabled(true);
            email.setAuthenticator(new DefaultAuthenticator(USERNAME, APP_PASSWORD));
            email.setFrom(USERNAME);
            email.addTo(to);
            email.setSubject(subject);
            email.setMsg(text);
            email.send();
        } catch (EmailException e) {
            throw new RuntimeException("Failed to send email to " + to, e);
        }
    }
}
