package com.truongphuc.controller;

import com.truongphuc.dto.FileUploadDto;
import com.truongphuc.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RequiredArgsConstructor
@RestController
public class Test {
    final private CloudinaryService cloudinaryService;

    @GetMapping(value = "/test")
    public FileUploadDto test (@RequestParam(name = "file") MultipartFile file) throws Exception {
        return cloudinaryService.uploadMessageFile(file);
    }
}
