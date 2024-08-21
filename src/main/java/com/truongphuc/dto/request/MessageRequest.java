package com.truongphuc.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
@Data
public class MessageRequest implements Serializable {
    @NotNull (message = "conversationId is required")
    @NotBlank (message = "conversationId is required")
    String conversationId;

    @NotNull (message = "content is required")
    @NotBlank (message = "content is required")
    String content;
}
