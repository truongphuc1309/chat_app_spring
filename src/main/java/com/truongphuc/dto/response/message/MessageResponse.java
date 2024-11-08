package com.truongphuc.dto.response.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.truongphuc.dto.response.file.FileResponse;
import com.truongphuc.dto.response.user.UserProfileResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageResponse implements Serializable {
    String id;
    String content;
    UserProfileResponse user;
    boolean active;
    String type;
    FileResponse file;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;
}