package com.truongphuc.service.impl;

import java.util.Optional;

import com.truongphuc.constant.ExceptionCode;
import com.truongphuc.dto.request.LogInRequest;
import com.truongphuc.dto.request.SignUpRequest;
import com.truongphuc.dto.response.LogInResponse;
import com.truongphuc.dto.response.SignUpResponse;
import com.truongphuc.entity.UserEntity;
import com.truongphuc.exception.AppException;
import com.truongphuc.mapper.AuthMapper;
import com.truongphuc.repository.UserRepository;
import com.truongphuc.service.AuthService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AuthServiceImpl implements AuthService{
    UserRepository userRepository;
    AuthMapper authMapper;
    AuthenticationManager authenticationManager;
    PasswordEncoder passwordEncoder;

    @Override
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        UserEntity userEntity =  authMapper.toUserEntity(signUpRequest);

        //Check email exist
        Optional<UserEntity> foundUser = userRepository.findByEmail(signUpRequest.getEmail());
        if (foundUser.isPresent())
            throw new AppException("Email is already in use", ExceptionCode.EXISTED_USER); 

        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));

        return authMapper.toSignUpResponse(userRepository.save(userEntity));

    }

    @Override
    public LogInResponse logIn(LogInRequest logInRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(logInRequest.getEmail(), logInRequest.getPassword()));
        Optional<UserEntity> foundUser = userRepository.findByEmail(logInRequest.getEmail());

        // if (!foundUser.isPresent())
        //     throw new AppException("Email or password incorrect", ExceptionCode.NON_EXISTED_USER);
        //
        // boolean isCorrect = passwordEncoder.matches(logInRequest.getPassword(), foundUser.get().getPassword()); 
        //
        // if (!isCorrect)
        //     throw new AppException("Email or password incorrect", ExceptionCode.UNAUTHORIZED);

        LogInResponse logInResponse = authMapper.toLogInResponse(foundUser.get());
        logInResponse.setAccessToken("access_token");
        
        log.info("::RESPONSE::{}" , logInResponse);

        return logInResponse;
    }

    
}
