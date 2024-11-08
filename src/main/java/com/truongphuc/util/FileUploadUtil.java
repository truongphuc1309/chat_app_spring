package com.truongphuc.util;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadUtil {
    void checkIsImage(MultipartFile file);
    void checkSize(MultipartFile file);
    String getExtension(MultipartFile file);
    String generateUniqueFileName(MultipartFile file);
}
