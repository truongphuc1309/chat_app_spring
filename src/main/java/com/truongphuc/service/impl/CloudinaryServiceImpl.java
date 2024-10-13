package com.truongphuc.service.impl;

import com.cloudinary.Cloudinary;
import com.truongphuc.dto.FileUploadDto;
import com.truongphuc.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@PropertySource(value = "classpath:application.properties")
@Service
public class CloudinaryServiceImpl implements CloudinaryService {
    final private Cloudinary cloudinary;

    @Value("${cloudinary.avatar.upload.preset}")
    private String avatarUploadPreset;

    @Value("${cloudinary.message.upload.preset}")
    private String messageUploadPreset;


    @Override
    public FileUploadDto upload(MultipartFile file, Map<String, String> opts) throws IOException {
        var result = cloudinary.uploader().upload(file.getBytes(), opts);

        String originalName = file.getOriginalFilename();
        String url = result.get("url").toString();
        String publicId = result.get("public_id").toString();
        String type = result.get("format").toString();
        return FileUploadDto.builder()
                .originalName(originalName)
                .url(url)
                .publicId(publicId)
                .type(type)
                .build();
    }

    @Override
    public FileUploadDto uploadMessageFile(MultipartFile file) throws IOException {
        Map<String, String> opts = Map.of("upload_preset", messageUploadPreset);
        return upload(file, opts);
    }

    @Override
    public FileUploadDto uploadAvatar(MultipartFile file) throws IOException {
        Map<String, String> opts = Map.of("upload_preset", avatarUploadPreset);
        return upload(file, opts);
    }

    @Override
    public void delete(String publicId) throws Exception {
       cloudinary.api().deleteResources(List.of(publicId), Map.of());
    }
}
