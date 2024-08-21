package com.truongphuc.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Entity (name = "message")
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "message-with-conversation",
                attributeNodes = {
                        @NamedAttributeNode(value = "conversation"),
                }
        ),

        @NamedEntityGraph(
                name = "message-with-user",
                attributeNodes = {
                        @NamedAttributeNode(value = "user")
                }
        ),

        @NamedEntityGraph(
                name = "message-with-user-and-conversation",
                attributeNodes = {
                        @NamedAttributeNode(value = "conversation", subgraph = "conversation-with-members"),
                        @NamedAttributeNode(value = "user")
                },
                subgraphs = {
                        @NamedSubgraph(
                                name = "conversation-with-members",
                                attributeNodes = {
                                        @NamedAttributeNode(value = "members"),
                                }
                        )
                }
        )

})

@Table (name = "message")
public class MessageEntity extends GenericEntity{
    @Column (name = "content")
    String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "user_id")
    UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "conversation_id")
    ConversationEntity conversation;
}
