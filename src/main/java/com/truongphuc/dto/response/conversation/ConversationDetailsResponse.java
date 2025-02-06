package com.truongphuc.dto.response.conversation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.truongphuc.dto.MemberDto;
import com.truongphuc.dto.response.user.UserProfileResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ConversationDetailsResponse implements Serializable {
    String id;

    String name;

    String avatar;

    boolean isGroup;

    UserProfileResponse createdBy;

    Set<MemberDto> members;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;
}
