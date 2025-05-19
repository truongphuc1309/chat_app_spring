package com.truongphuc.service.impl;

import com.truongphuc.constant.ExceptionCode;
import com.truongphuc.constant.TokenType;
import com.truongphuc.dto.TokenPairDto;
import com.truongphuc.entity.RefreshTokenEntity;
import com.truongphuc.exception.AppException;
import com.truongphuc.repository.RefreshTokenRepository;
import com.truongphuc.service.JwtService;
import com.truongphuc.service.RedisService;
import com.truongphuc.service.UserService;
import com.truongphuc.util.TimeUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
import java.util.function.Function;

@RequiredArgsConstructor
@PropertySource(value = "classpath:application.properties")
@Service
public class JwtServiceImpl implements JwtService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final RedisService redisService;
    private final TimeUtil timeUtil;

    @Value("${jwt.secretkey}")
    private String secretKey;

    @Value("${jwt.refreshkey}")
    private String refreshKey;

    @Value("${jwt.verifykey}")
    private String verifyKey;

    @Value("${jwt.resetkey}")
    private String resetKey;

    @Value("${jwt.duration.access}")
    private String durationAccess;

    @Value("${jwt.duration.refresh}")
    private String durationRefresh;

    @Value("${jwt.duration.verify}")
    private String durationVerify;

    @Value("${jwt.duration.reset}")
    private String durationReset;

    @Override
    public TokenPairDto createTokenPair(UserDetails userDetails) {
        String accessToken = generateAccessToken(userDetails);
        String refreshToken = generateRefreshToken(userDetails);

        String email = userDetails.getUsername();
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .email(email)
                .value(refreshToken)
                .expiredAt(extractExpiration(TokenType.REFRESH_TOKEN, refreshToken))
                .build();

        refreshTokenRepository.save(refreshTokenEntity);

        return TokenPairDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(TokenType.ACCESS_TOKEN, userDetails, durationAccess);
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(TokenType.REFRESH_TOKEN, userDetails, durationRefresh);
    }

    @Override
    public String generateVerifyToken(UserDetails userDetails) {
        return generateToken(TokenType.VERIFY_TOKEN, userDetails, durationVerify);
    }

    @Override
    public String generateResetToken(UserDetails userDetails) {
        return generateToken(TokenType.RESET_TOKEN, userDetails, durationReset);
    }

    @Override
    public String generateToken(TokenType tokenType, UserDetails userDetails, String duration) {
        return Jwts.builder()
                .setIssuer("com.truongphuc")
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + timeUtil.parseDurationToMillis(duration)))
                .signWith(getKey(tokenType), SignatureAlgorithm.HS512)
                .compact();
    }

    @Override
    public boolean verify(TokenType tokenType, String token) {
        String extractedEmail = extractEmail(tokenType, token);
        UserDetails foundUser = userService.getUserDetailsService().loadUserByUsername(extractedEmail);
        if (foundUser == null) return false;

        String foundToken = redisService.findValueByKey(token);

        if (foundToken != null && tokenType.equals(TokenType.ACCESS_TOKEN))
            return false;

        if (isExpired(tokenType, token))
            throw new AppException("Expired token", ExceptionCode.EXPIRED_TOKEN);

        return true;
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

        switch (tokenType) {
            case ACCESS_TOKEN: {
                key = secretKey;
                break;
            }
            case REFRESH_TOKEN: {
                key = refreshKey;
                break;
            }

            case VERIFY_TOKEN: {
                key = verifyKey;
                break;
            }

            case RESET_TOKEN: {
                key = resetKey;
                break;
            }
        }

        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public Claims parseToken(TokenType tokenType, String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(getKey(tokenType)).build().parseClaimsJws(token).getBody();
        }
        catch (ExpiredJwtException e) {
            e.printStackTrace();
            throw new AppException("Expired token", ExceptionCode.EXPIRED_TOKEN);
        }catch (Exception e){
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
