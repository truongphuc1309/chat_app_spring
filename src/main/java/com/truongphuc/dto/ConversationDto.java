package com.truongphuc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.truongphuc.dto.response.user.UserProfileResponse;
import com.truongphuc.entity.FileUploadEntity;
import com.truongphuc.entity.UserEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.enterprise.context.Conversation;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ConversationDto implements Serializable {
    String id;

    String name;

    FileUploadEntity avatar;

    boolean isGroup;

    UserEntity createdBy;

    Set<MemberDto> members;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;
}