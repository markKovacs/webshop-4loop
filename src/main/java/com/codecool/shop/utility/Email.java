package com.codecool.shop.utility;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import java.io.StringWriter;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import static com.codecool.shop.Config.*;

public class Email {

    private static final String PATH = "src/main/resources/templates/";

    public static void send(String to, String body, String subject) {

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
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=UTF-8");

            Transport.send(message);
            System.out.println("E-mail message was sent successfully.");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    public static String renderEmailTemplate(String template, Map<String, Object> params) {
        TemplateEngine templateEngine = new TemplateEngine();
        FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setTemplateMode("HTML");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheable(false);
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context();
        context.setVariable("params", params);
        StringWriter stringWriter = new StringWriter();
        templateEngine.process(PATH + template, context, stringWriter);
        return stringWriter.toString();
    }

}
