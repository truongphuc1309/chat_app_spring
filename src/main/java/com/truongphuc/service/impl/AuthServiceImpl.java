package com.truongphuc.service.impl;

import java.io.UnsupportedEncodingException;
import java.time.LocalTime;
import java.util.Optional;

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
import com.truongphuc.entity.ResetPasswordTokenEntity;
import com.truongphuc.entity.TokenEntity;
import com.truongphuc.entity.UserEntity;
import com.truongphuc.entity.VerifyTokenEntity;
import com.truongphuc.exception.AppException;
import com.truongphuc.mapper.AuthMapper;
import com.truongphuc.repository.ResetPasswordTokenRepository;
import com.truongphuc.repository.TokenRepository;
import com.truongphuc.repository.UserRepository;
import com.truongphuc.repository.VerifyTokenRepository;
import com.truongphuc.service.AuthService;
import com.truongphuc.service.JwtService;
import com.truongphuc.service.MailService;
import com.truongphuc.service.UserService;

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
    TokenRepository tokenRepository;
    VerifyTokenRepository verifyTokenRepository;
    ResetPasswordTokenRepository resetPasswordTokenRepository;
    UserService userService;
    JwtService jwtService;
    MailService mailService;

    @Override
    public SignUpResponse signUp(SignUpRequest signUpRequest) throws MessagingException, UnsupportedEncodingException {
        UserEntity userEntity =  authMapper.toUserEntity(signUpRequest);

        //Check email exist
        Optional<UserEntity> foundUser = userRepository.findByEmail(signUpRequest.getEmail());
        if (foundUser.isPresent())
            throw new AppException("Email is already in use", ExceptionCode.EXISTED_USER); 

        userEntity.setActive(false);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));

        // Generate verify key and send email
        String verifyToken = jwtService.generateVerifyToken(userEntity);
        VerifyTokenEntity newVerifyToken = VerifyTokenEntity.builder()
                .value(verifyToken)
                .email(userEntity.getEmail())
                .build();

        verifyTokenRepository.save(newVerifyToken);

        mailService.sendVerificationEmail(userEntity.getEmail(), verifyToken);
        return authMapper.toSignUpResponse(userRepository.save(userEntity));

    }

    @Override
    public LogInResponse logIn(LogInRequest logInRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(logInRequest.getEmail(), logInRequest.getPassword()));
        Optional<UserEntity> foundUser = userRepository.findByEmail(logInRequest.getEmail());

        if (foundUser.isEmpty())
            throw new AppException("Email or Password is incorrect", ExceptionCode.EXISTED_USER);

        if (!foundUser.get().isActive())
            throw new AppException("Unverified account", ExceptionCode.INACTIVE_USER);

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
        Optional<TokenEntity> foundToken = tokenRepository.findByEmailAndToken(email, refreshToken);
        if (foundToken.isEmpty())
            throw new AppException("Token not found", ExceptionCode.INVALID_TOKEN);

        String accessToken = jwtService.generateAccessToken(foundUser);
        foundToken.get().setAccessToken(accessToken);
        tokenRepository.save(foundToken.get());


        return authMapper.toRefreshResponse(foundToken.get());
    }

    @Override
    public void logOut(String accessToken) {
        boolean isValid = jwtService.verify(TokenType.ACCESS_TOKEN, accessToken);
        if (!isValid) throw new AppException("Invalid Token", ExceptionCode.UNAUTHORIZED);

        String email = jwtService.extractEmail(TokenType.ACCESS_TOKEN, accessToken);
        Optional<TokenEntity> foundTokens = tokenRepository.findByEmailAndToken(email, accessToken);
        foundTokens.ifPresent(tokenRepository::delete);
    }

    @Override
    public VerifyResponse verifyEmail(String verifyToken) {
        Optional<VerifyTokenEntity> foundToken = verifyTokenRepository.findByValue(verifyToken);
        if (foundToken.isEmpty() || jwtService.isExpired(TokenType.VERIFY_TOKEN, verifyToken))
            throw new AppException("Invalid Token", ExceptionCode.INVALID_TOKEN);


        String email = jwtService.extractEmail(TokenType.VERIFY_TOKEN, verifyToken);

        if (!foundToken.get().getEmail().equals(email))
            throw new AppException("Invalid Token", ExceptionCode.INVALID_TOKEN);

        Optional<UserEntity> foundUser = userRepository.findByEmail(email);
        if (foundUser.isEmpty() || foundUser.get().isActive())
            throw new AppException("Invalid Token", ExceptionCode.INVALID_TOKEN);

        foundUser.get().setActive(true);
        userRepository.save(foundUser.get());
        verifyTokenRepository.delete(foundToken.get());

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

        Optional<VerifyTokenEntity> foundToken = verifyTokenRepository.findByEmail(email);


        //Limit request email in 15 mins
        if (foundToken.isPresent()){
            LocalTime mailCreateTime = LocalTime.from(foundToken.get().getUpdatedAt());
            LocalTime now = LocalTime.now();
            if (now.getMinute() - mailCreateTime.getMinute() < 15)
                throw new AppException("Please retry after 15 minutes", ExceptionCode.OVER_LIMIT);
        }

        VerifyTokenEntity  token = foundToken.orElseGet(() -> VerifyTokenEntity.builder()
                .email(email)
                .build());





        String verifyToken = jwtService.generateVerifyToken(foundUser.get());
        token.setValue(verifyToken);
        verifyTokenRepository.save(token);

        //Send email
        mailService.sendVerificationEmail(email, verifyToken);


        return email;
    }

    @Override
    public String forgotPassword(String email) throws MessagingException, UnsupportedEncodingException {
        Optional<UserEntity> foundUser = userRepository.findByEmail(email);

        if (foundUser.isEmpty() || !foundUser.get().isActive())
            throw new AppException("Invalid Email", ExceptionCode.INACTIVE_USER);



        String resetToken = jwtService.generateResetToken(foundUser.get());

        Optional<ResetPasswordTokenEntity> foundToken = resetPasswordTokenRepository.findByEmail(email);

        if (foundToken.isPresent()) {
            // Limit mail request in 15 mins
            LocalTime mailCreateTime = LocalTime.from(foundToken.get().getUpdatedAt());
            LocalTime now = LocalTime.now();
            if (now.getMinute() - mailCreateTime.getMinute() < 15)
                throw new AppException("Please retry after 15 minutes", ExceptionCode.OVER_LIMIT);

            foundToken.get().setValue(resetToken);
            resetPasswordTokenRepository.save(foundToken.get());
        }else {
            ResetPasswordTokenEntity newToken = ResetPasswordTokenEntity.builder()
                    .value(resetToken)
                    .email(email)
                    .build();
            resetPasswordTokenRepository.save(newToken);
        }

        // Send email
        mailService.sendResetPasswordEmail(email, resetToken);

        return email;
    }

    @Override
    public String confirmResetPassword(String resetToken) {
        Optional<ResetPasswordTokenEntity> foundToken = resetPasswordTokenRepository.findByValue(resetToken);
        if (foundToken.isEmpty() || jwtService.isExpired(TokenType.RESET_TOKEN, resetToken))
            throw new AppException("Invalid Token", ExceptionCode.INVALID_TOKEN);

        String email = jwtService.extractEmail(TokenType.RESET_TOKEN, resetToken);
        Optional<UserEntity> foundUser = userRepository.findByEmail(email);
        if (foundUser.isEmpty() || !foundUser.get().isActive())
            throw new AppException("Invalid User", ExceptionCode.INVALID_TOKEN);
        return "Confirmed";
    }

    @Override
    public String resetPassword(String resetToken, ResetPasswordRequest request) {
        String newPassword = request.getNewPassword();
        String confirmPassword = request.getConfirmPassword();

        if (!newPassword.equals(confirmPassword))
            throw  new AppException("Passwords do not match", ExceptionCode.INVALID_ARGUMENT);

        Optional<ResetPasswordTokenEntity> foundToken = resetPasswordTokenRepository.findByValue(resetToken);
        if (foundToken.isEmpty() || jwtService.isExpired(TokenType.RESET_TOKEN, resetToken))
            throw new AppException("Invalid Token", ExceptionCode.INVALID_TOKEN);

        String email = jwtService.extractEmail(TokenType.RESET_TOKEN, resetToken);
        if (!foundToken.get().getEmail().equals(email))
            throw new AppException("Invalid Token", ExceptionCode.INVALID_TOKEN);

        Optional<UserEntity> foundUser = userRepository.findByEmail(email);
        if (foundUser.isEmpty() || !foundUser.get().isActive())
            throw new AppException("Invalid User", ExceptionCode.INVALID_TOKEN);

        foundUser.get().setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(foundUser.get());
        resetPasswordTokenRepository.delete(foundToken.get());

        return email;
    }

}
