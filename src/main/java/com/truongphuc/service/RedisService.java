package com.truongphuc.service;

import com.truongphuc.constant.TokenType;

import java.util.Date;

public interface RedisService {
    String findValueByKey(String key);

    void createKeyValuePair(String key, String value, Date expiration);

    void addTokenToBlacklist(TokenType tokenType,String token, Date expiration);


    boolean deleteTokenByUserId(TokenType tokenType, String userId);

    boolean deleteKey (String key);

    String createKey (TokenType tokenType, String id);
}