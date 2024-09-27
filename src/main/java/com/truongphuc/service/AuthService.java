package com.truongphuc.service;

import com.truongphuc.dto.request.LogInRequest;
import com.truongphuc.dto.request.ResetPasswordRequest;
import com.truongphuc.dto.request.SignUpRequest;
import com.truongphuc.dto.response.LogInResponse;
import com.truongphuc.dto.response.RefreshResponse;
import com.truongphuc.dto.response.SignUpResponse;
import com.truongphuc.dto.response.VerifyResponse;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

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
