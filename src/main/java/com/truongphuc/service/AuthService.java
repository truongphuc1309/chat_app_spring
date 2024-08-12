package com.truongphuc.service;

import com.truongphuc.dto.request.LogInRequest;
import com.truongphuc.dto.request.SignUpRequest;
import com.truongphuc.dto.response.LogInResponse;
import com.truongphuc.dto.response.RefreshResponse;
import com.truongphuc.dto.response.SignUpResponse;

public interface AuthService {
    SignUpResponse signUp (SignUpRequest signUpRequest);
    LogInResponse logIn (LogInRequest logInRequest);
    RefreshResponse refresh (String refreshToken);
    void logOut (String accessToken);
}
