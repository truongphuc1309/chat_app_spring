package com.truongphuc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.truongphuc.dto.request.auth.LogInRequest;
import com.truongphuc.dto.response.auth.LogInResponse;
import com.truongphuc.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthControllerTest {
    private final AuthService authService = mock(AuthService.class);

    private final AuthController authController = new AuthController(authService);

    private final MockMvc mockMvc =  MockMvcBuilders.standaloneSetup(authController).build();

    @Test
    void testLogIn_Success() throws Exception {
        LogInRequest logInRequest = new LogInRequest("test@example.com", "password123");
        LogInResponse logInResponse = new LogInResponse("1", "test@example.com", "Test User", "accessToken", "refreshToken", null, null);
        when(authService.logIn(logInRequest)).thenReturn(logInResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(logInRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value("0000"));
    }
}