package com.truongphuc.dto.response.conversation;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class ConversationAvatarChangeResponse {
    String conversationId;
    String avatar;
}
