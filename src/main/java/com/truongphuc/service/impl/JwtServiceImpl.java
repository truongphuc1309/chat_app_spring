package com.truongphuc.service.impl;

import com.truongphuc.constant.ExceptionCode;
import com.truongphuc.constant.TokenType;
import com.truongphuc.entity.TokenEntity;
import com.truongphuc.exception.AppException;
import com.truongphuc.repository.TokenRepository;
import com.truongphuc.service.JwtService;
import com.truongphuc.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
@PropertySource(value = "classpath:application.properties")
@Service
public class JwtServiceImpl implements JwtService {
    private final TokenRepository tokenRepository;
    private final UserService userService;

    @Value("${jwt.secretkey}")
    private String secretKey;

    @Value("${jwt.refreshkey}")
    private String refreshKey;

    @Value("${jwt.duration.access.hours}")
    private long durationAccessHours;

    @Value("${jwt.duration.refresh.hours}")
    private long durationRefreshHours;

    @Override
    public TokenEntity createTokenPair(UserDetails userDetails) {
        TokenEntity result = null;

        String accessToken = generateAccessToken(userDetails);
        String refreshToken = generateRefreshToken(userDetails);

        String email = userDetails.getUsername();
        Optional<TokenEntity> foundToken = tokenRepository.findByEmail(email);

        if (foundToken.isPresent()) {
            foundToken.get().setAccessToken(accessToken);
            foundToken.get().setRefreshToken(refreshToken);
            tokenRepository.save(foundToken.get());

            result = foundToken.get();
        }else{
            result = TokenEntity.builder()
                    .email(email)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
            tokenRepository.save(result);
        }

        return result;
    }

    @Override
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(TokenType.ACCESS_TOKEN, userDetails, durationAccessHours);
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(TokenType.REFRESH_TOKEN, userDetails, durationRefreshHours);
    }

    @Override
    public String generateToken(TokenType tokenType, UserDetails userDetails, long durationHours) {
        return Jwts.builder()
                .setIssuer("com.truongphuc")
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * durationHours))
                .signWith(getKey(tokenType), SignatureAlgorithm.HS512)
                .compact();
    }

    @Override
    public boolean verify(TokenType tokenType, String token) {
        String extractedEmail = extractEmail(tokenType, token);
        UserDetails foundUser = userService.getUserDetailsService().loadUserByUsername(extractedEmail);
        if (foundUser == null) return false;

        Optional<TokenEntity> foundToken = tokenRepository.findByEmail(extractedEmail);
        String correctToken = null;

        if (foundToken.isEmpty()) {
            return false;
        }else{
            if (tokenType.equals(TokenType.ACCESS_TOKEN))
                correctToken = foundToken.get().getAccessToken();
            else
                correctToken = foundToken.get().getRefreshToken();

            return correctToken.equals(token) && !isExpired(tokenType, correctToken);
        }
    }

    @Override
    public boolean isExpired(TokenType tokenType, String token) {
        Date expirationDate = extractExpiration(tokenType, token);
        Date now = new Date(System.currentTimeMillis());

        return expirationDate.before(now);
    }

    @Override
    public Key getKey(TokenType tokenType) {
        String key = "";
        if (tokenType.equals(TokenType.ACCESS_TOKEN))
            key = secretKey;
        else
            key = refreshKey;

        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public Claims parseToken(TokenType tokenType, String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(getKey(tokenType)).build().parseClaimsJws(token).getBody();
        }
        catch (Exception e) {
            throw new AppException("No matching token", ExceptionCode.INVALID_TOKEN);
        }
    }

    @Override
    public <T> T extractClaim(TokenType tokenType, String token, Function<Claims, T> claimsResolver) {
        Claims parsedClaims = parseToken(tokenType,token);
        return claimsResolver.apply(parsedClaims);
    }

    @Override
    public String extractEmail(TokenType tokenType, String token) {
        return extractClaim(tokenType, token, Claims::getSubject);
    }

    @Override
    public Date extractExpiration(TokenType tokenType, String token) {
        return extractClaim(tokenType, token, Claims::getExpiration);
    }
}
