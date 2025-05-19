package com.truongphuc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder

@Entity(name = "refreshToken")
@Table(name = "refresh_token")
public class RefreshTokenEntity extends GenericEntity {
    @Column(name = "email")
    String email;

    @Column(name="value")
    String value;

    @Column(name = "expired_at")
    Date expiredAt;
}
