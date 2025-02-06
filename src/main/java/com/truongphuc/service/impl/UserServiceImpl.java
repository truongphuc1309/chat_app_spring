package com.truongphuc.service.impl;

import com.truongphuc.constant.ExceptionCode;
import com.truongphuc.dto.request.conversation.ChangePasswordRequest;
import com.truongphuc.dto.request.user.UserUpdateRequest;
import com.truongphuc.dto.response.user.UserProfileResponse;
import com.truongphuc.entity.FileUploadEntity;
import com.truongphuc.entity.UserEntity;
import com.truongphuc.exception.AppException;
import com.truongphuc.mapper.UserMapper;
import com.truongphuc.repository.UserRepository;
import com.truongphuc.service.FileUploadService;
import com.truongphuc.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserServiceImpl implements UserService {
    
    UserRepository userRepository;
    UserMapper userMapper;
    FileUploadService fileUploadService;


    @Override
    public UserDetailsService getUserDetailsService() {
        return email -> 
            userRepository.findByEmail(email)
            .orElseThrow(() -> new AppException("Email or Password is incorrect", ExceptionCode.NON_EXISTED_USER));
    }

    @Override
    public UserProfileResponse getUserProfile(String email) {
        Optional<UserEntity> foundUser = userRepository.findByEmail(email);

        if (foundUser.isEmpty())
            throw  new AppException("Invalid user", ExceptionCode.NON_EXISTED_USER);

        return userMapper.toUserProfileResponse(foundUser.get());
    }

    @Override
    public UserProfileResponse updateUser(String email, UserUpdateRequest userUpdateRequest) {
        String name = userUpdateRequest.getName();
        String avatar = userUpdateRequest.getAvatar();

        if (name == null && avatar == null)
            throw new AppException("Invalid argument", ExceptionCode.INVALID_ARGUMENT);

        Optional<UserEntity> foundUser = userRepository.findByEmail(email);

        if (foundUser.isEmpty())
            throw new AppException("Invalid user", ExceptionCode.NON_EXISTED_USER);

        if (name != null )
            if (!name.trim().isBlank())
                foundUser.get().setName(name);

//        if (avatar != null)
//            if (!avatar.trim().isBlank())
//                foundUser.get().setAvatar(avatar);

        userRepository.save(foundUser.get());

        return userMapper.toUserProfileResponse(foundUser.get());
    }

    @Override
    public UserProfileResponse updateAvatar(String email, MultipartFile avatar) throws Exception {
        Optional<UserEntity> foundUser = userRepository.findByEmail(email);

        if (foundUser.isEmpty())
            throw new AppException("Invalid user", ExceptionCode.NON_EXISTED_USER);

        FileUploadEntity oldAvatar = foundUser.get().getAvatar();

        UserEntity updatedUser = fileUploadService.uploadUserAvatar(foundUser.get(), avatar);

        var persistedUser = userRepository.save(updatedUser);

        // Check and delete old avatar
        if (oldAvatar != null) fileUploadService.deleteFile(oldAvatar);

        return userMapper.toUserProfileResponse(persistedUser);
    }

    @Override
    public void removeAvatar(String email) throws Exception {
        Optional<UserEntity> foundUser = userRepository.findByEmail(email);

        if (foundUser.isEmpty())
            throw new AppException("Invalid user", ExceptionCode.NON_EXISTED_USER);

        FileUploadEntity oldAvatar = foundUser.get().getAvatar();

        foundUser.get().setAvatar(null);
        userRepository.save(foundUser.get());

        if (oldAvatar != null) fileUploadService.deleteFile(oldAvatar);
    }

    @Override
    public List<UserProfileResponse> searchUser(String key) {
        List<UserEntity> result = userRepository.findAllByKey(key);
        return userMapper.toUserProfileResponseList(result);
    }


    @Override
    public String changePassword(String userEmail, ChangePasswordRequest changePasswordRequest) {
        Optional<UserEntity> foundUser = userRepository.findByEmail(userEmail);
        if (foundUser.isEmpty() || !foundUser.get().isActive())
            throw new AppException("Invalid user", ExceptionCode.INACTIVE_USER);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean isMatch = passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), foundUser.get().getPassword());

        if (!isMatch)
            throw new AppException("Invalid password", ExceptionCode.INVALID_ARGUMENT);

        foundUser.get().setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(foundUser.get());

        return "changed";
    }

    @Override
    public UserProfileResponse updateUserStatus(String email, boolean online) {
        Optional<UserEntity> foundUser = userRepository.findByEmail(email);
        if (foundUser.isEmpty() || !foundUser.get().isActive())
            throw new AppException("Invalid user", ExceptionCode.INACTIVE_USER);

        if (foundUser.get().isOnline() != online){
            foundUser.get().setOnline(online);
            var result = userRepository.save(foundUser.get());
            return userMapper.toUserProfileResponse(result);
        }

        return userMapper.toUserProfileResponse(foundUser.get());
    }

}
