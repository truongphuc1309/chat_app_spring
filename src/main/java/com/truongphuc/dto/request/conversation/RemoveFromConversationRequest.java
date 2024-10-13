package com.truongphuc.dto.request.conversation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class RemoveFromConversationRequest implements Serializable {
    @NotBlank(message = "conversationId is required")
    @NotNull(message = "conversationId is required")
    String conversationId;

    @NotBlank (message = "memberId is required")
    @NotNull (message = "memberId is required")
    String memberId;
}
