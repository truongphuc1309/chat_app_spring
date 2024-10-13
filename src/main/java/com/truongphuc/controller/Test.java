package com.truongphuc.controller;

import com.truongphuc.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class Test {
    final private CloudinaryService cloudinaryService;

    @GetMapping(value = "/test")
    public String test (@RequestParam(name = "id") String id) throws Exception {
        cloudinaryService.delete(id);
        return "true";
    }
}
