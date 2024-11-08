package com.truongphuc.service;

import com.truongphuc.dto.FileUploadDto;
import com.truongphuc.entity.ConversationEntity;
import com.truongphuc.entity.FileUploadEntity;
import com.truongphuc.entity.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileUploadService {
    FileUploadDto uploadFileMessage(MultipartFile file) throws IOException;
    FileUploadEntity uploadAvatar(MultipartFile file) throws IOException;
    UserEntity uploadUserAvatar(UserEntity userEntity, MultipartFile avatar) throws Exception;
    ConversationEntity uploadConversationAvatar(ConversationEntity conversation, MultipartFile avatar) throws Exception;
    void deleteFile(FileUploadEntity fileUploadEntity) throws Exception;
}
