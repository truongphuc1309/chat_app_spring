
package com.truongphuc.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults (level = AccessLevel.PRIVATE)
@Entity(name = "user")
@Table (name = "user")

public class UserEntity extends GenericEntity implements UserDetails{
    @Column (name = "email", unique = true)
    String email;
    
    @Column (name = "name")
    String name;
    
    @Column (name = "password")
    String password;

    @Column (name = "active")
    boolean active;

    @Column (name="online")
    boolean online;

    @EqualsAndHashCode.Exclude
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn (name = "avatar_file_id")
    FileUploadEntity avatar;

//    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
//    @EqualsAndHashCode.Exclude
//    List<ConversationEntity> createdConversations = new ArrayList<>();

//    @OneToMany (mappedBy = "user", fetch = FetchType.LAZY)
//    @EqualsAndHashCode.Exclude
//    List<MessageEntity> messages = new ArrayList<>();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
