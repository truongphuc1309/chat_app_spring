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
                        @NamedAttributeNode(value = "user", subgraph = "user-with-avatar"),
                        @NamedAttributeNode(value = "file")
                },
                subgraphs = {
                        @NamedSubgraph(
                                name = "user-with-avatar",
                                attributeNodes = {
                                        @NamedAttributeNode(value = "avatar"),
                                }
                        )
                }
        ),

//        @NamedEntityGraph(
//                name = "message-with-user-and-conversation",
//                attributeNodes = {
//                        @NamedAttributeNode(value = "conversation", subgraph = "conversation-with-members"),
//                        @NamedAttributeNode(value = "user", subgraph = "user-with-avatar"),
//                        @NamedAttributeNode(value = "file")
//                },
//                subgraphs = {
//                        @NamedSubgraph(
//                                name = "conversation-with-members",
//                                attributeNodes = {
//                                        @NamedAttributeNode(value = "members"),
//                                }
//                        ),
//                        @NamedSubgraph(
//                                name = "user-with-avatar",
//                                attributeNodes = {
//                                        @NamedAttributeNode(value = "avatar"),
//                                }
//                        )
//                }
//        )

})

@Table (name = "message")
public class MessageEntity extends GenericEntity{
    @Column (name = "content")
    String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn (name = "user_id")
    UserEntity user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn (name = "conversation_id")
    ConversationEntity conversation;

    @Column(name = "type")
    String type;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn (name = "file_id")
    FileUploadEntity file;

    @Column(name = "seq")
    long seq;

    @Column(name = "active")
    boolean active;
}
