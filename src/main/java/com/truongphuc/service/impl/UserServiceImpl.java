package com.truongphuc.service.impl;

import com.truongphuc.constant.ExceptionCode;
import com.truongphuc.exception.AppException;
import com.truongphuc.repository.UserRepository;
import com.truongphuc.service.UserService;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserServiceImpl implements UserService {
    
    UserRepository userRepository;

    @Override
    public UserDetailsService getUserDetailsService() {
        return email -> 
            userRepository.findByEmail(email)
            .orElseThrow(() -> new AppException("Email or Password is incorrect", ExceptionCode.NON_EXISTED_USER));
    }

}
