package com.truongphuc.dto.request.conversation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ConversationAvatarChangeRequest {
    @NotBlank(message = "conversationId is required")
    @NotNull(message = "conversationId is required")
    String conversationId;

    @NotBlank (message = "avatar is required")
    @NotNull (message = "avatar is required")
    MultipartFile avatar;
}
