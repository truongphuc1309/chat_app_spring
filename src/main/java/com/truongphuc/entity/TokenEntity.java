package com.truongphuc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@Entity

@Table(name = "token")
public class TokenEntity extends GenericEntity{
    @Column (name = "email", unique = true)
    String email;

    @Column (name = "access_token")
    String accessToken;

    @Column(name = "refresh_token")
    String refreshToken;
}
