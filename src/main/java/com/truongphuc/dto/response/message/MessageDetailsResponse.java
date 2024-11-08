package com.truongphuc.dto.response.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.truongphuc.dto.response.file.FileResponse;
import com.truongphuc.dto.response.user.UserProfileResponse;
import com.truongphuc.dto.response.conversation.ConversationResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageDetailsResponse implements Serializable {
    String id;
    String content;
    UserProfileResponse user;
    ConversationResponse conversation;
    String type;
    FileResponse file;
    boolean active;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;
}
