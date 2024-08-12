package com.truongphuc.controller;

import com.truongphuc.dto.request.LogInRequest;
import com.truongphuc.dto.request.SignUpRequest;
import com.truongphuc.dto.response.ApiResponse;
import com.truongphuc.dto.response.LogInResponse;
import com.truongphuc.dto.response.RefreshResponse;
import com.truongphuc.dto.response.SignUpResponse;
import com.truongphuc.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PropertySource(value = "classpath:application.properties")
@RequiredArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
@RestController
@RequestMapping ("/auth")
public class AuthController {

    final AuthService authService;

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

    @PostMapping("/refresh")
    private ApiResponse<RefreshResponse> refresh (HttpServletRequest request) {
        String refreshToken = request.getHeader("Referer");

        RefreshResponse result = authService.refresh(refreshToken);

        return new ApiResponse<>("0000", "Success Refresh", result);
    }

    @PostMapping("/logout")
    private ApiResponse<String> logout (HttpServletRequest request) {
        String accessToken = request.getHeader("x-param");
        authService.logOut(accessToken);


        return new ApiResponse<>("0000", "Success", "Success Log Out");
    }
    
}
