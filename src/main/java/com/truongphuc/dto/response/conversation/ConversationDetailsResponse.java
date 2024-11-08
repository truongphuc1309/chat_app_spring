package com.truongphuc.dto.response.conversation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.truongphuc.dto.response.user.UserProfileResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ConversationDetailsResponse implements Serializable {
    String id;

    String name;

    String avatar;

    boolean isGroup;

    UserProfileResponse createdBy;

    Set<UserProfileResponse> members;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;
}
