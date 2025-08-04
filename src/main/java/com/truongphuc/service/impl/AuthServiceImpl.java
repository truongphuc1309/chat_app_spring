package com.truongphuc.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Optional;

import com.truongphuc.dto.TokenPairDto;
import com.truongphuc.entity.*;
import com.truongphuc.repository.*;
import com.truongphuc.service.*;
import jakarta.mail.MessagingException;

import com.truongphuc.constant.ExceptionCode;
import com.truongphuc.constant.TokenType;
import com.truongphuc.dto.request.auth.LogInRequest;
import com.truongphuc.dto.request.auth.ResetPasswordRequest;
import com.truongphuc.dto.request.auth.SignUpRequest;
import com.truongphuc.dto.response.auth.LogInResponse;
import com.truongphuc.dto.response.auth.RefreshResponse;
import com.truongphuc.dto.response.auth.SignUpResponse;
import com.truongphuc.dto.response.auth.VerifyResponse;
import com.truongphuc.exception.AppException;
import com.truongphuc.mapper.AuthMapper;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
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
    UserService userService;
    JwtService jwtService;
    MailService mailService;
    RedisService redisService;
    RefreshTokenRepository refreshTokenRepository;

    @Override
    public SignUpResponse signUp(SignUpRequest signUpRequest) throws MessagingException, UnsupportedEncodingException {
        UserEntity userEntity =  authMapper.toUserEntity(signUpRequest);

        //Check email exist
        Optional<UserEntity> foundUser = userRepository.findByEmail(signUpRequest.getEmail());
        if (foundUser.isPresent())
            throw new AppException("Email is already in use", ExceptionCode.EXISTED_USER); 

        userEntity.setActive(false);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        var persistedUser = userRepository.save(userEntity);

        // Generate verify key and send email
        String verifyToken = jwtService.generateVerifyToken(userEntity);
        String tokenKey = redisService.createKey(TokenType.VERIFY_TOKEN, persistedUser.getId());
        Date expiration = jwtService.extractExpiration(TokenType.VERIFY_TOKEN, verifyToken);
        redisService.createKeyValuePair(tokenKey, verifyToken, expiration);

        mailService.sendVerificationEmail(userEntity.getEmail(), verifyToken);
        return authMapper.toSignUpResponse(persistedUser);

    }

    @Override
    public LogInResponse logIn(LogInRequest logInRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(logInRequest.getEmail(), logInRequest.getPassword()));
        Optional<UserEntity> foundUser = userRepository.findByEmail(logInRequest.getEmail());

        if (foundUser.isEmpty())
            throw new AppException("Email or Password is incorrect", ExceptionCode.EXISTED_USER);

        if (!foundUser.get().isActive())
            throw new AppException("Unverified account", ExceptionCode.INACTIVE_USER);

        TokenPairDto tokenPair = jwtService.createTokenPair(foundUser.get());

        LogInResponse logInResponse = authMapper.toLogInResponse(foundUser.get());
        logInResponse.setAccessToken(tokenPair.getAccessToken());
        logInResponse.setRefreshToken(tokenPair.getRefreshToken());

        return logInResponse;
    }

    @Override
    public RefreshResponse refresh(String accessToken, String refreshToken) {
        boolean isValid = jwtService.verify(TokenType.REFRESH_TOKEN, refreshToken);

        if (!isValid) throw new AppException("Invalid Token", ExceptionCode.UNAUTHORIZED);

        String email = jwtService.extractEmail(TokenType.REFRESH_TOKEN, refreshToken);
        UserDetails foundUser = userService.getUserDetailsService().loadUserByUsername(email);
        Optional<RefreshTokenEntity> foundToken = refreshTokenRepository.findByValue(refreshToken);
        if (foundToken.isEmpty())
            throw new AppException("Token not found", ExceptionCode.INVALID_TOKEN);

        try {
            Date oldAccessTokenExpiration = jwtService.extractExpiration(TokenType.ACCESS_TOKEN, accessToken);
            redisService.addTokenToBlacklist(TokenType.ACCESS_TOKEN, accessToken, oldAccessTokenExpiration);
        } catch (Exception ignored){
        }


        String newAccessToken = jwtService.generateAccessToken(foundUser);


        return RefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void logOut(String accessToken, String refreshToken) {
        boolean isValid = jwtService.verify(TokenType.ACCESS_TOKEN, accessToken);
        if (!isValid) throw new AppException("Invalid Token", ExceptionCode.UNAUTHORIZED);

        Date expiration = jwtService.extractExpiration(TokenType.ACCESS_TOKEN, accessToken);
        Optional<RefreshTokenEntity> foundRefreshToken = refreshTokenRepository.findByValue(refreshToken);
        if (foundRefreshToken.isEmpty())
            throw new AppException("Invalid token", ExceptionCode.INVALID_TOKEN);

        refreshTokenRepository.delete(foundRefreshToken.get());
        redisService.addTokenToBlacklist(TokenType.ACCESS_TOKEN,accessToken, expiration);
    }

    @Override
    public VerifyResponse verifyEmail(String verifyToken) {
        String email = jwtService.extractEmail(TokenType.VERIFY_TOKEN, verifyToken);
        Optional<UserEntity> foundUser = userRepository.findByEmail(email);
        if (foundUser.isEmpty() || foundUser.get().isActive())
            throw new AppException("Invalid Token", ExceptionCode.INVALID_TOKEN);

        String tokenKey = redisService.createKey(TokenType.VERIFY_TOKEN, foundUser.get().getId());
        String foundToken = redisService.findValueByKey(tokenKey);
        if (foundToken == null || !foundToken.equals(verifyToken) || !jwtService.verify(TokenType.VERIFY_TOKEN, verifyToken))
            throw new AppException("Invalid Token", ExceptionCode.INVALID_TOKEN);


        foundUser.get().setActive(true);
        userRepository.save(foundUser.get());
        redisService.deleteKey(tokenKey);

        return VerifyResponse.builder()
                .email(email)
                .verified(true)
                .build();
    }

    @Override
    public String sendVerificationEmail(String email) throws MessagingException, UnsupportedEncodingException {
        Optional<UserEntity> foundUser = userRepository.findByEmail(email);

        if (foundUser.isEmpty() || foundUser.get().isActive())
            throw new AppException("Invalid Email", ExceptionCode.INACTIVE_USER);

        String tokenKey = redisService.createKey(TokenType.VERIFY_TOKEN, foundUser.get().getId());
        if (redisService.findValueByKey(tokenKey) != null)
            redisService.deleteKey(tokenKey);

        //Limit request email in 15 mins
//        if (foundToken.isPresent()){
//            LocalTime mailCreateTime = LocalTime.from(foundToken.get().getUpdatedAt());
//            LocalTime now = LocalTime.now();
//            if (now.getMinute() - mailCreateTime.getMinute() < 15)
//                throw new AppException("Please retry after 15 minutes", ExceptionCode.OVER_LIMIT);
//        }



        String verifyToken = jwtService.generateVerifyToken(foundUser.get());
        Date expiration = jwtService.extractExpiration(TokenType.VERIFY_TOKEN, verifyToken);
        redisService.createKeyValuePair(tokenKey, verifyToken, expiration);

        //Send email
        mailService.sendVerificationEmail(email, verifyToken);


        return email;
    }

    @Override
    public String forgotPassword(String email) throws MessagingException, UnsupportedEncodingException {
        Optional<UserEntity> foundUser = userRepository.findByEmail(email);

        if (foundUser.isEmpty() || !foundUser.get().isActive())
            throw new AppException("Invalid Email", ExceptionCode.INACTIVE_USER);

        String tokenKey = redisService.createKey(TokenType.RESET_TOKEN, foundUser.get().getId());
        if (redisService.findValueByKey(tokenKey) != null)
            redisService.deleteKey(tokenKey);

        String resetToken = jwtService.generateResetToken(foundUser.get());
        Date expiration = jwtService.extractExpiration(TokenType.RESET_TOKEN, resetToken);
        redisService.createKeyValuePair(tokenKey, resetToken, expiration);

        // Send email
        mailService.sendResetPasswordEmail(email, resetToken);

        return email;
    }

    @Override
    public String confirmResetPassword(String resetToken) {
        String email = jwtService.extractEmail(TokenType.RESET_TOKEN, resetToken);
        Optional<UserEntity> foundUser = userRepository.findByEmail(email);
        if (foundUser.isEmpty() || !foundUser.get().isActive())
            throw new AppException("Invalid User", ExceptionCode.INVALID_TOKEN);

        String tokenKey = redisService.createKey(TokenType.RESET_TOKEN, foundUser.get().getId());
        String foundToken = redisService.findValueByKey(tokenKey);
        if (foundToken == null || !foundToken.equals(resetToken) || !jwtService.verify(TokenType.RESET_TOKEN, resetToken))
            throw new AppException("Invalid Token", ExceptionCode.INVALID_TOKEN);

        return "Confirmed";
    }

    @Override
    public String resetPassword(String resetToken, ResetPasswordRequest request) {
        String newPassword = request.getNewPassword();
        String confirmPassword = request.getConfirmPassword();

        if (!newPassword.equals(confirmPassword))
            throw  new AppException("Passwords do not match", ExceptionCode.INVALID_ARGUMENT);

        String email = jwtService.extractEmail(TokenType.RESET_TOKEN, resetToken);
        Optional<UserEntity> foundUser = userRepository.findByEmail(email);
        if (foundUser.isEmpty() || !foundUser.get().isActive())
            throw new AppException("Invalid User", ExceptionCode.INVALID_TOKEN);

        String tokenKey = redisService.createKey(TokenType.RESET_TOKEN, foundUser.get().getId());
        String foundToken = redisService.findValueByKey(tokenKey);
        if (foundToken == null || !foundToken.equals(resetToken) || !jwtService.verify(TokenType.RESET_TOKEN, resetToken))
            throw new AppException("Invalid Token", ExceptionCode.INVALID_TOKEN);


        foundUser.get().setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(foundUser.get());
        redisService.deleteKey(tokenKey);

        return email;
    }

}
