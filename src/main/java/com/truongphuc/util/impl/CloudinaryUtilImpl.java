package com.truongphuc.util.impl;

import com.truongphuc.util.CloudinaryUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource(value = "classpath:application.properties")
public class CloudinaryUtilImpl implements CloudinaryUtil {
    @Value("${cloudinary.url.console}")
    String cloudinaryUrl;

    @Value("${cloudinary.url.download.post}")
    String downloadPostUrl;

    @Value("${cloudinary.url.download.pre}")
    String downloadPreUrl;

    @Value("${cloudinary.cloud.name}")
    String cloudName;

    @Override
    public String generateDownloadUrl(String assetId) {
        return cloudinaryUrl + cloudName + "/" + downloadPreUrl + "/" + assetId + "/" + downloadPostUrl;
    }
}
