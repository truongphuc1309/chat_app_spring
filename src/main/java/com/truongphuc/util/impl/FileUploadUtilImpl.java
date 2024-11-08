package com.truongphuc.util.impl;

import com.truongphuc.constant.ExceptionCode;
import com.truongphuc.exception.AppException;
import com.truongphuc.util.FileUploadUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;


@PropertySource(value = "classpath:application.properties")
@Service
public class FileUploadUtilImpl implements FileUploadUtil {
    @Value("${upload.maxsize.mb}")
    private long maxSizeMB;

    final private String IMAGE_REGEX = "(jpg|jpeg|png|gif|webp)$";

    @Override
    public void checkIsImage(MultipartFile file) {
        if (!Pattern.matches(IMAGE_REGEX, getExtension(file).toLowerCase()))
            throw new AppException("Invalid file type", ExceptionCode.INVALID_FILE_TYPE);
    }

    @Override
    public void checkSize(MultipartFile file) {
        if (file.getSize() > maxSizeMB * 1024 * 1024)
            throw new AppException("Your attached file must less than 25Mb", ExceptionCode.INVALID_FILE_SIZE);
    }

    @Override
    public String getExtension(MultipartFile file) {
        return FilenameUtils.getExtension(file.getOriginalFilename());
    }

    @Override
    public String generateUniqueFileName(MultipartFile file) {
        String sortUuid = UUID.randomUUID().toString().substring(0, 8);
        String dateString = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String fileName = FilenameUtils.getBaseName(file.getOriginalFilename());
        return fileName + "_" + dateString + "_" + sortUuid;
    }
}
