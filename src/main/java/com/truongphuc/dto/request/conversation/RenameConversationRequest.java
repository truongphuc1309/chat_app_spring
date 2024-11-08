package com.truongphuc.dto.request.conversation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class RenameConversationRequest implements Serializable {

    @NotBlank (message = "conversationId is required")
    @NotNull (message = "conversationId is required")
    String conversationId;

    @NotBlank (message = "newName is required")
    @NotNull (message = "newName is required")
    String newName;
}
