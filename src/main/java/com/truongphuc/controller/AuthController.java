package com.truongphuc.controller;

import java.io.UnsupportedEncodingException;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

import com.truongphuc.dto.request.auth.LogInRequest;
import com.truongphuc.dto.request.auth.ResetPasswordRequest;
import com.truongphuc.dto.request.auth.SignUpRequest;
import com.truongphuc.dto.response.ApiResponse;
import com.truongphuc.dto.response.auth.LogInResponse;
import com.truongphuc.dto.response.auth.RefreshResponse;
import com.truongphuc.dto.response.auth.SignUpResponse;
import com.truongphuc.dto.response.auth.VerifyResponse;
import com.truongphuc.service.AuthService;

import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@PropertySource(value = "classpath:application.properties")
@RequiredArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
@RestController
@RequestMapping ("/auth")
public class AuthController {

    final AuthService authService;

    @PostMapping ("/signup")
    ApiResponse<SignUpResponse> signUp(@RequestBody @Valid SignUpRequest signUpRequest) throws MessagingException, UnsupportedEncodingException {
        SignUpResponse result = authService.signUp(signUpRequest);

        return new ApiResponse<>("0000", "Success Sign Up", result);
    }

    @PostMapping ("/login")
    ApiResponse<LogInResponse> logIn(@RequestBody @Valid LogInRequest logInRequest) {
        LogInResponse result = authService.logIn(logInRequest);

        return new ApiResponse<>("0000", "Success Log In", result);
    }

    @PostMapping ("/verify")
    private ApiResponse<VerifyResponse> verifyEmail (@RequestHeader("X-Referer") String verifyToken) {
        VerifyResponse result = authService.verifyEmail(verifyToken);

        return new ApiResponse<>("0000", "Success verify email", result);
    }

    @PostMapping("/send-email")
    private ApiResponse<String> sendVerificationEmail (@RequestBody String email) throws MessagingException, UnsupportedEncodingException {

        String result = authService.sendVerificationEmail(email);

        return new ApiResponse<>("0000", "Success send email", result);
    }

    @PostMapping("/forgot-password")
    private ApiResponse<String> forgotPassword (@RequestBody String email) throws MessagingException, UnsupportedEncodingException {

        String result = authService.forgotPassword(email);

        return new ApiResponse<>("0000", "Success send email", result);
    }

    @PostMapping ("/confirm-reset-password")
    private ApiResponse<String> resetPassword (@RequestHeader("X-Referer") String resetToken) {
        String result = authService.confirmResetPassword (resetToken);
        return new ApiResponse<>("0000", "Success confirm", result);
    }

    @PostMapping ("/reset-password")
    private ApiResponse<String> resetPassword (@RequestHeader("X-Referer") String resetToken,
                                               @RequestBody @Valid ResetPasswordRequest request) {

        String result = authService.resetPassword(resetToken, request);
        return new ApiResponse<>("0000", "Success reset password", result);
    }


    @PostMapping("/refresh")
    private ApiResponse<RefreshResponse> refresh (@RequestHeader("Referer") String refreshToken) {

        RefreshResponse result = authService.refresh(refreshToken);

        return new ApiResponse<>("0000", "Success Refresh", result);
    }


    @PostMapping("/logout")
    ApiResponse<String> logout(@RequestHeader("x-param") String accessToken) {
        authService.logOut(accessToken);
        return new ApiResponse<>("0000", "Success", "Success Log Out");
    }
    
}
