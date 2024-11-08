package com.truongphuc.service;

import com.truongphuc.dto.FileUploadDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    FileUploadDto upload(MultipartFile file, Map<String, String> opts) throws IOException;
    FileUploadDto uploadMessageFile(MultipartFile file) throws IOException;
    FileUploadDto uploadAvatar(MultipartFile file) throws IOException;
    void delete(String publicId) throws Exception;
}
