package com.truongphuc.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults (level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@Data
@Entity(name = "conversation")


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

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn (name = "createdBy")
    UserEntity createdBy;

//    @EqualsAndHashCode.Exclude
//    @OneToMany (mappedBy = "conversation", cascade = CascadeType.REMOVE)
//    List<MessageEntity> messages;
}

















