package com.truongphuc.dto.request.message;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
@Builder
@Data
public class MessageRequest implements Serializable {
    @NotNull (message = "conversationId is required")
    @NotBlank (message = "conversationId is required")
    String conversationId;

    @NotNull (message = "content is required")
    @NotBlank (message = "content is required")
    String content;

    @NotNull (message = "content is required")
    @NotBlank (message = "content is required")
    String type;

    MultipartFile file;
}
