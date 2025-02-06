package com.truongphuc.service;

import java.io.UnsupportedEncodingException;

import jakarta.mail.MessagingException;

import com.truongphuc.dto.request.auth.LogInRequest;
import com.truongphuc.dto.request.auth.ResetPasswordRequest;
import com.truongphuc.dto.request.auth.SignUpRequest;
import com.truongphuc.dto.response.auth.LogInResponse;
import com.truongphuc.dto.response.auth.RefreshResponse;
import com.truongphuc.dto.response.auth.SignUpResponse;
import com.truongphuc.dto.response.auth.VerifyResponse;

public interface AuthService {
    SignUpResponse signUp (SignUpRequest signUpRequest) throws MessagingException, UnsupportedEncodingException;

    LogInResponse logIn (LogInRequest logInRequest);

    RefreshResponse refresh (String refreshToken);

    void logOut (String accessToken);

    VerifyResponse verifyEmail(String verifyToken);

    String sendVerificationEmail(String email) throws MessagingException, UnsupportedEncodingException;

    String forgotPassword(String email) throws MessagingException, UnsupportedEncodingException;

    String confirmResetPassword(String resetToken);

    String resetPassword(String resetToken, ResetPasswordRequest request);
}
