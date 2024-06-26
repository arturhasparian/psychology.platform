package com.psychology.product.service.impl;

import com.psychology.product.repository.model.User;
import com.psychology.product.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailSenderServiceImpl implements MailService {

    @Value(("${spring.mail.username}"))
    private String username;
    @Value(("${server.frontend.host}"))
    private String frontendHost;

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public MailSenderServiceImpl(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    public void sendSimpleMessage(String emailTo, String subject, String templateName, Context context) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(username);
        helper.setTo(emailTo);
        helper.setSubject(subject);

        String htmlContent = templateEngine.process(templateName, context);
        helper.setText(htmlContent, true);

        javaMailSender.send(mimeMessage);
    }

    @Override
    public void sendRegistrationMail(User user) throws MessagingException {
        Context context = new Context();
        context.setVariable("username", user.getFirstName());
        context.setVariable("host", frontendHost);
        context.setVariable("code", user.getUniqueCode());
        sendSimpleMessage(user.getEmail(), "Registration", "activation_message", context);
    }

    @Override
    public void sendResetPasswordMail(User user) throws MessagingException {
        Context context = new Context();
        context.setVariable("username", user.getFirstName());
        context.setVariable("host", frontendHost);
        context.setVariable("code", user.getUniqueCode());
        sendSimpleMessage(user.getEmail(), "Reset password", "reset_password_message", context);
    }
}
