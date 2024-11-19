package com.truongphuc.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@FieldDefaults (level = AccessLevel.PRIVATE)
@Entity (name = "participant")

@Table (name = "participant")
public class ParticipantEntity extends GenericEntity {

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    ConversationEntity conversation;

    @ManyToOne
    @JoinColumn (name = "user_id")
    UserEntity user;

    @Column (name = "last_read")
    long lastRead;
}
