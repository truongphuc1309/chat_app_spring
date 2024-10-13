package com.truongphuc.dto.response.auth;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class LogInResponse implements Serializable{
    String id;
    String email;
    String name;
    String accessToken;
    String refreshToken;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;
}
