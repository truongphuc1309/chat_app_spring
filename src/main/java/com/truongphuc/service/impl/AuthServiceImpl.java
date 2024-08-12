package com.truongphuc.service.impl;

import com.truongphuc.constant.ExceptionCode;
import com.truongphuc.constant.TokenType;
import com.truongphuc.dto.request.LogInRequest;
import com.truongphuc.dto.request.SignUpRequest;
import com.truongphuc.dto.response.LogInResponse;
import com.truongphuc.dto.response.RefreshResponse;
import com.truongphuc.dto.response.SignUpResponse;
import com.truongphuc.entity.TokenEntity;
import com.truongphuc.entity.UserEntity;
import com.truongphuc.exception.AppException;
import com.truongphuc.mapper.AuthMapper;
import com.truongphuc.repository.TokenRepository;
import com.truongphuc.repository.UserRepository;
import com.truongphuc.service.AuthService;
import com.truongphuc.service.JwtService;
import com.truongphuc.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AuthServiceImpl implements AuthService{
    UserRepository userRepository;
    AuthMapper authMapper;
    AuthenticationManager authenticationManager;
    PasswordEncoder passwordEncoder;
    TokenRepository tokenRepository;
    UserService userService;
    JwtService jwtService;

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

        if (foundUser.isEmpty())
            throw new AppException("Email or Password is incorrect", ExceptionCode.EXISTED_USER);

        TokenEntity tokenPair = jwtService.createTokenPair(foundUser.get());

        LogInResponse logInResponse = authMapper.toLogInResponse(foundUser.get());
        logInResponse.setAccessToken(tokenPair.getAccessToken());
        logInResponse.setRefreshToken(tokenPair.getRefreshToken());

        return logInResponse;
    }

    @Override
    public RefreshResponse refresh(String refreshToken) {
        boolean isValid = jwtService.verify(TokenType.REFRESH_TOKEN, refreshToken);

        if (!isValid) throw new AppException("Invalid Token", ExceptionCode.UNAUTHORIZED);

        String email = jwtService.extractEmail(TokenType.REFRESH_TOKEN, refreshToken);
        UserDetails foundUser = userService.getUserDetailsService().loadUserByUsername(email);
        Optional<TokenEntity> foundToken = tokenRepository.findByEmail(email);
        String accessToken = jwtService.generateAccessToken(foundUser);
        if (foundToken.isPresent()) {
            foundToken.get().setAccessToken(accessToken);
            tokenRepository.save(foundToken.get());
        }

        return authMapper.toRefreshResponse(foundToken.get());
    }

    @Override
    public void logOut(String accessToken) {
        boolean isValid = jwtService.verify(TokenType.ACCESS_TOKEN, accessToken);
        if (!isValid) throw new AppException("Invalid Token", ExceptionCode.UNAUTHORIZED);

        String email = jwtService.extractEmail(TokenType.ACCESS_TOKEN, accessToken);
        Optional<TokenEntity> foundTokens = tokenRepository.findByEmail(email);

        foundTokens.ifPresent(tokenRepository::delete);
    }


}
