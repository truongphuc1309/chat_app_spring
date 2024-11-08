package com.truongphuc.service.impl;

import com.cloudinary.Cloudinary;
import com.truongphuc.dto.FileUploadDto;
import com.truongphuc.service.CloudinaryService;
import com.truongphuc.util.CloudinaryUtil;
import com.truongphuc.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@PropertySource(value = "classpath:application.properties")
@Service
public class CloudinaryServiceImpl implements CloudinaryService {
    final private Cloudinary cloudinary;
    final private CloudinaryUtil cloudinaryUtil;
    final private FileUploadUtil fileUploadUtil;

    @Value("${cloudinary.avatar.upload.preset}")
    private String avatarUploadPreset;

    @Value("${cloudinary.message.upload.preset}")
    private String messageUploadPreset;



    @Override
    public FileUploadDto upload(MultipartFile file, Map<String, String> opts) throws IOException {
        String originalName = file.getOriginalFilename();
        String format =  fileUploadUtil.getExtension(file);

        String uniqueName = fileUploadUtil.generateUniqueFileName(file);

        Map<String, String> mainOpts = new HashMap<>();
        mainOpts.put("public_id", uniqueName);
        mainOpts.put("format", format);
        mainOpts.putAll(opts);

        var result = cloudinary.uploader().upload(file.getBytes(), mainOpts);

        String url = result.get("url").toString();
        String publicId = result.get("public_id").toString();
        String assetId = result.get("asset_id").toString();
        long size = Long.parseLong(result.get("bytes").toString());
        String downloadUrl = cloudinaryUtil.generateDownloadUrl(assetId);


        return FileUploadDto.builder()
                .assetId(assetId)
                .size(size)
                .downloadUrl(downloadUrl)
                .originalName(originalName)
                .url(url)
                .publicId(publicId)
                .type(format)
                .build();
    }

    @Override
    public FileUploadDto uploadMessageFile(MultipartFile file) throws IOException {
        Map<String, String> opts = Map.of("upload_preset", messageUploadPreset, "resource_type", "auto");
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
