package com.truongphuc.controller;

import com.truongphuc.service.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RequiredArgsConstructor
@RestController
public class Test {
    final private MailService mailService;

    @GetMapping("/auth/test")
    public boolean test () throws MessagingException, UnsupportedEncodingException {
        mailService.sendResetPasswordEmail("truongngo1309@gmail.com", "123456");
        return true;
    }
}
