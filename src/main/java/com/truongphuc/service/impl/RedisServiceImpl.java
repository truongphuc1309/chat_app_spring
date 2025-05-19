package com.truongphuc.service.impl;

import com.truongphuc.constant.TokenType;
import com.truongphuc.service.JwtService;
import com.truongphuc.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public String findValueByKey(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void createKeyValuePair(String key, String value, Date expiration) {
        Date now = new Date();
        long diff = (expiration.getTime() - now.getTime()) / 1000;
        redisTemplate.opsForValue().set(key, value, diff, TimeUnit.SECONDS);
    }

    @Override
    public void addTokenToBlacklist(TokenType tokenType, String token, Date expiration) {
        String value = "BLACKLISTED_" + tokenType.name();
        createKeyValuePair(token, value, expiration);
    }

    @Override
    public boolean deleteTokenByUserId(TokenType tokenType, String userId) {
        String key = createKey(tokenType, userId);
        String foundToken = redisTemplate.opsForValue().get(key);
        if (foundToken == null)
            return false;

        return deleteKey(key);
    }


    @Override
    public boolean deleteKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    @Override
    public String createKey(TokenType tokenType, String id) {
        return tokenType.name() + "_" + id;
    }
}
