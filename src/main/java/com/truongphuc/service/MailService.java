package com.truongphuc.service;


import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface MailService {
    boolean sendEmail(String recipients, String subject, String content, MultipartFile[] files) throws MessagingException, UnsupportedEncodingException;
    void sendEmailWithLink (String recipient, String subject, Map<String, Object> properties, String template) throws MessagingException, UnsupportedEncodingException;
    void sendVerificationEmail(String recipient, String token) throws MessagingException, UnsupportedEncodingException;
    void sendResetPasswordEmail(String recipient, String token) throws MessagingException, UnsupportedEncodingException;
}
