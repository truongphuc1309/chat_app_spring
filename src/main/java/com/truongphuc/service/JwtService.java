package com.truongphuc.service;

import com.truongphuc.constant.TokenType;
import com.truongphuc.dto.TokenPairDto;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;


public interface JwtService {
    TokenPairDto createTokenPair (UserDetails userDetails);

    String generateAccessToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    String generateVerifyToken(UserDetails userDetails);

    String generateResetToken (UserDetails userDetails);

    String generateToken(TokenType tokenType, UserDetails userDetails, String duration);

    boolean verify(TokenType tokenType, String token);

    boolean isExpired(TokenType tokenType, String token);

    Key getKey(TokenType tokenType);

    Claims parseToken(TokenType tokenType, String token);

    <T> T extractClaim(TokenType tokenType, String token, Function<Claims, T> claimsResolver);

    String extractEmail(TokenType tokenType, String token);

    Date extractExpiration(TokenType tokenType, String token);
}
