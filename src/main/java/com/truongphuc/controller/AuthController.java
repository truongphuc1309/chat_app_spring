package com.truongphuc.controller;

import jakarta.validation.Valid;

import com.truongphuc.dto.request.LogInRequest;
import com.truongphuc.dto.request.SignUpRequest;
import com.truongphuc.dto.response.ApiResponse;
import com.truongphuc.dto.response.LogInResponse;
import com.truongphuc.dto.response.SignUpResponse;
import com.truongphuc.service.AuthService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping ("/auth")
public class AuthController {
    AuthService authService;

    @PostMapping ("/signup")
    private ApiResponse<SignUpResponse> signUp (@RequestBody @Valid SignUpRequest signUpRequest) {
        SignUpResponse result = authService.signUp(signUpRequest);

        return new ApiResponse<SignUpResponse>("0000", "Success Sign Up", result);
    }

    @PostMapping ("/login")
    private ApiResponse<LogInResponse> logIn (@RequestBody @Valid LogInRequest logInRequest) {
        LogInResponse result = authService.logIn(logInRequest);

        return new ApiResponse<LogInResponse>("0000", "Success Log In", result);
    }
    
}
