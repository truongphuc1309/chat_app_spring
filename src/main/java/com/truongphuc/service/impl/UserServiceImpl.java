package com.truongphuc.service.impl;

import com.truongphuc.constant.ExceptionCode;
import com.truongphuc.dto.request.UserUpdateRequest;
import com.truongphuc.dto.response.UserProfileResponse;
import com.truongphuc.entity.UserEntity;
import com.truongphuc.exception.AppException;
import com.truongphuc.mapper.UserMapper;
import com.truongphuc.repository.UserRepository;
import com.truongphuc.service.UserService;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserServiceImpl implements UserService {
    
    UserRepository userRepository;
    UserMapper userMapper;

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

        if (avatar != null)
            if (!avatar.trim().isBlank())
                foundUser.get().setAvatar(avatar);

        userRepository.save(foundUser.get());

        return userMapper.toUserProfileResponse(foundUser.get());
    }

    @Override
    public List<UserProfileResponse> searchUser(String key) {
        List<UserEntity> result = userRepository.findAllByKey(key);
        return userMapper.toUserProfileResponseList(result);
    }

}
