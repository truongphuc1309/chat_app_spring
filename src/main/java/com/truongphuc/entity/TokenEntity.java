package com.truongphuc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
@Data
@Builder

@Entity (name = "token")
@Table(name = "token")
public class TokenEntity extends GenericEntity{
    @Column (name = "email")
    String email;

    @Column (name = "access_token")
    String accessToken;

    @Column(name = "refresh_token")
    String refreshToken;
}
