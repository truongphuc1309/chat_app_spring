package com.truongphuc.controller;

import com.truongphuc.dto.response.ApiResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<String> getHelloPage() {
        return new ApiResponse<String>("1000", "Success", "Siuuu1234!!!");
    }
}
