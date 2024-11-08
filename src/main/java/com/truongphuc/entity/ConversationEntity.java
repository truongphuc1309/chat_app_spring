package com.truongphuc.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults (level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@Data
@Entity(name = "conversation")

@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "conversation-with-members",
                attributeNodes = {
                        @NamedAttributeNode(value = "members", subgraph = "members-with-avatar"),
                        @NamedAttributeNode(value = "createdBy"),
                        @NamedAttributeNode(value = "avatar")
                },

                subgraphs = {
                @NamedSubgraph(
                        name = "members-with-avatar",
                        attributeNodes = {
                                @NamedAttributeNode(value = "avatar"),
                        }
                )
        }
        ),

        @NamedEntityGraph(
                name = "conversation-with-createdBy",
                attributeNodes = {
                        @NamedAttributeNode(value = "createdBy"),
                        @NamedAttributeNode(value = "avatar"),
                }
        )

})



@Table (name = "conversation")
public class ConversationEntity extends GenericEntity{

    @Column (name = "name")
    String name;

    @EqualsAndHashCode.Exclude
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "avatar_file_id")
    FileUploadEntity avatar;

    @Column (name = "is_group")
    boolean isGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @Fetch(FetchMode.SELECT)
    @JoinColumn (name = "createdBy")
    UserEntity createdBy;

    @ManyToMany(fetch = FetchType.LAZY)
    @Fetch(FetchMode.SELECT)
    @JoinTable (name = "user_conversation", joinColumns = @JoinColumn(name = "conversation_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    @EqualsAndHashCode.Exclude
    Set<UserEntity> members;

    @OneToMany (mappedBy = "conversation", cascade = CascadeType.REMOVE)
    List<MessageEntity> messages;
}

















