package com.truongphuc.dto.response.conversation;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class RenameConversationResponse implements Serializable {
    String conversationId;
    String oldName;
    String newName;
}
