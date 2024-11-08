package com.truongphuc.dto.request.conversation;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ConversationCreationRequest implements Serializable {
    String name;

    @NotNull(message = "isGroup is required")
    boolean group;

    @NotNull (message = "members are required")
    Set<String> addedMembers;
}
