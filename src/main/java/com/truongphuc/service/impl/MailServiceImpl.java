package com.truongphuc.service.impl;

import com.truongphuc.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import javax.security.auth.Subject;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@PropertySource(value = "classpath:application.properties")

@Service
public class MailServiceImpl implements MailService {
    final private JavaMailSender mailSender;
    final private SpringTemplateEngine templateEngine;

    @Value("${spring.mail.from}")
    private String emailFrom;

    @Value("${client.verifyemail.url}")
    private String verificationLink;

    @Value("${client.resetpassword.url}")
    private String resetPasswordLink;


    @Override
    public boolean sendEmail(String recipients, String subject, String content, MultipartFile[] files) throws MessagingException, UnsupportedEncodingException {
        log.info("Email is sending ...");

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(emailFrom, "Chatty");

        if (recipients.contains(",")) { // send to multiple users
            helper.setTo(InternetAddress.parse(recipients));
        } else { // send to single user
            helper.setTo(recipients);
        }

        // Send attach files
        if (files != null) {
            for (MultipartFile file : files) {
                helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), file);
            }
        }

        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);

        log.info("Email has sent to successfully, recipients: {}", recipients);

        return true;
    }

    @Override
    public void sendEmailWithLink(String recipient, String subject, Map<String, Object> properties, String template) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();

        context.setVariables(properties);

        helper.setFrom(emailFrom, "Chatty");
        helper.setTo(recipient);
        helper.setSubject(subject);
        String html = templateEngine.process(template, context);
        helper.setText(html, true);

        mailSender.send(message);
    }

    @Override
    public void sendVerificationEmail(String recipient, String token) throws MessagingException, UnsupportedEncodingException {
        log.info("Sending verification email link to user, email={}", recipient);

        Map<String, Object> properties = new HashMap<>();
        String link = verificationLink + token;
        properties.put("verificationLink", link);

        sendEmailWithLink(recipient, "Verify email", properties, "verify-email");
        log.info("Confirming link has sent to user, email={}, linkConfirm={}", recipient, link);

    }

    @Override
    public void sendResetPasswordEmail(String recipient, String token) throws MessagingException, UnsupportedEncodingException {
        log.info("Sending reset password link to user, email={}", recipient);

        Map<String, Object> properties = new HashMap<>();
        String link = resetPasswordLink + token;
        properties.put("resetPasswordLink", link);

        sendEmailWithLink(recipient, "Reset password", properties, "reset-password");
        log.info("Reset password  has sent to user, email={}, linkConfirm={}", recipient, link);
    }
}
