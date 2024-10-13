package com.truongphuc.service.impl;

import com.truongphuc.constant.ExceptionCode;
import com.truongphuc.constant.TokenType;
import com.truongphuc.entity.TokenEntity;
import com.truongphuc.exception.AppException;
import com.truongphuc.repository.TokenRepository;
import com.truongphuc.service.JwtService;
import com.truongphuc.service.UserService;
import io.jsonwebtoken.*;
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

    @Value("${jwt.verifykey}")
    private String verifyKey;

    @Value("${jwt.resetkey}")
    private String resetKey;

    @Value("${jwt.duration.access.hours}")
    private long durationAccessHours;

    @Value("${jwt.duration.refresh.hours}")
    private long durationRefreshHours;

    @Value("${jwt.duration.verify.hours}")
    private long durationVerifyHours;

    @Value("${jwt.duration.reset.mins}")
    private long durationResetMins;

    @Override
    public TokenEntity createTokenPair(UserDetails userDetails) {
        TokenEntity result = null;

        String accessToken = generateAccessToken(userDetails);
        String refreshToken = generateRefreshToken(userDetails);

        String email = userDetails.getUsername();

//        Optional<TokenEntity> foundToken = tokenRepository.findByEmail(email);
//
//        if (foundToken.isPresent()) {
//            foundToken.get().setAccessToken(accessToken);
//            foundToken.get().setRefreshToken(refreshToken);
//            tokenRepository.save(foundToken.get());
//
//            result = foundToken.get();
//        }else{
//            result = TokenEntity.builder()
//                    .email(email)
//                    .accessToken(accessToken)
//                    .refreshToken(refreshToken)
//                    .build();
//            tokenRepository.save(result);
//        }
//
//        return result;

        TokenEntity newToken = TokenEntity.builder()
                .email(email)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        result = tokenRepository.save(newToken);
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
    public String generateVerifyToken(UserDetails userDetails) {
        return generateToken(TokenType.VERIFY_TOKEN, userDetails, durationVerifyHours);
    }

    @Override
    public String generateResetToken(UserDetails userDetails) {
        return Jwts.builder()
                .setIssuer("com.truongphuc")
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * durationResetMins))
                .signWith(getKey(TokenType.RESET_TOKEN), SignatureAlgorithm.HS512)
                .compact();
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

        Optional<TokenEntity> foundToken = tokenRepository.findByEmailAndToken(extractedEmail, token);
        String correctToken = null;

        if (foundToken.isEmpty())
            return false;

        if (tokenType.equals(TokenType.ACCESS_TOKEN))
            correctToken = foundToken.get().getAccessToken();
        else
            correctToken = foundToken.get().getRefreshToken();

        if(!correctToken.equals(token))
            throw new AppException("Invalid token", ExceptionCode.INVALID_TOKEN);

        if (isExpired(tokenType, correctToken))
            throw new AppException("Expired toke", ExceptionCode.EXPIRED_TOKEN);

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
