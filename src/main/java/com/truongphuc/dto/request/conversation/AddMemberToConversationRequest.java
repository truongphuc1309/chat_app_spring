package com.truongphuc.dto.request.conversation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
@Data
public class AddMemberToConversationRequest {
    @NotNull (message = "conversationId is required")
    @NotBlank(message = "conversationId is required")
    String conversationId;

    @NotNull (message = "userId is required")
    @NotBlank(message = "userId is required")
    String userId;
}
