package com.codecool.shop.utility;

import com.codecool.shop.order.Order;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import java.io.StringWriter;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import static com.codecool.shop.Config.*;

public class Email {

    public static void send(Order order) {

        String to = order.getEmail();
        String body = renderOrderTemplate(order);
        String subject = "Order Confirmation";

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
            System.out.println("Wmail message sent successfully.");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    public static String renderOrderTemplate(Order userOrder) {
        TemplateEngine templateEngine = new TemplateEngine();
        FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setTemplateMode("HTML");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheable(false);
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context();
        context.setVariable("order", userOrder);
        StringWriter stringWriter = new StringWriter();
        templateEngine.process("src/main/resources/templates/email", context, stringWriter);
        return stringWriter.toString();
    }

}
