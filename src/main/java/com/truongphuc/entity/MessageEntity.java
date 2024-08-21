package com.truongphuc.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity (name = "message")
@Table (name = "message")
public class MessageEntity extends GenericEntity{
    @Column (name = "content")
    String content;

    @ManyToOne
    @JoinColumn (name = "user_id")
    UserEntity user;

    @ManyToOne
    @JoinColumn (name = "conversation_id")
    ConversationEntity conversation;

}
