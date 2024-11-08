package com.truongphuc.config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;

public class MultipartConfig {
    public static MultipartConfigElement getMultipartConfigElement() {
        MultipartConfigFactory multipartConfigFactory = new MultipartConfigFactory();
//        multipartConfigFactory.setMaxFileSize(DataSize.parse("25MB"));
//        multipartConfigFactory.setMaxRequestSize(DataSize.parse("25MB"));

        return multipartConfigFactory.createMultipartConfig();
    }

}
