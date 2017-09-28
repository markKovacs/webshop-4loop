package com.codecool.shop.utility;


import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import static com.codecool.shop.Config.*;


public class Email {


    public static void send(String to, String subject, String body) {

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.socketFactory.port", SMTP_PORT);
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SMTP_USER,SMTP_PASS);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            // message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);


            //message.setText("Dear Mail Crawler,");
            message.setContent(body, "text/html; charset=UTF-8");

            Transport.send(message);

            System.out.println("Sent message successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

}
