package com.truongphuc.service.impl;

import com.truongphuc.dto.FileUploadDto;
import com.truongphuc.entity.ConversationEntity;
import com.truongphuc.entity.FileUploadEntity;
import com.truongphuc.entity.UserEntity;
import com.truongphuc.mapper.FileUploadMapper;
import com.truongphuc.repository.FileUploadRepository;
import com.truongphuc.service.CloudinaryService;
import com.truongphuc.service.FileUploadService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class FileUploadServiceImpl implements FileUploadService {
    CloudinaryService cloudinaryService;
    FileUploadRepository fileUploadRepository;
    FileUploadMapper fileUploadMapper;

    @Override
    public FileUploadEntity uploadFileMessage(MultipartFile file) throws IOException {
        // upload on cloudinary
        FileUploadDto uploadedFileDto = cloudinaryService.uploadMessageFile(file);
        FileUploadEntity newFileUpload = fileUploadMapper.toFileUploadEntity(uploadedFileDto);
        // persist upload file
        return fileUploadRepository.save(newFileUpload);
    }

    @Override
    public FileUploadEntity uploadAvatar(MultipartFile file) throws IOException {
        // upload on cloudinary
        FileUploadDto uploadedFileDto = cloudinaryService.uploadAvatar(file);
        FileUploadEntity newFileUpload = fileUploadMapper.toFileUploadEntity(uploadedFileDto);

        // persist upload file
        return fileUploadRepository.save(newFileUpload);
    }

    @Override
    public UserEntity uploadUserAvatar(UserEntity user, MultipartFile avatar) throws Exception {
        FileUploadEntity uploadedFile = this.uploadAvatar(avatar);

        user.setAvatar(uploadedFile);
        return user;
    }

    @Override
    public ConversationEntity uploadConversationAvatar(ConversationEntity conversation, MultipartFile avatar) throws Exception {
        FileUploadEntity uploadedFile = this.uploadAvatar(avatar);
        conversation.setAvatar(uploadedFile);
        return conversation;
    }


    @Override
    public void deleteFile(FileUploadEntity file) throws Exception {
        cloudinaryService.delete(file.getPublicId());
        fileUploadRepository.delete(file);
    }
}
