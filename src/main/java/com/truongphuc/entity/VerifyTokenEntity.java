package com.truongphuc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;


@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@Entity

@Table(name = "verify_token")
public class VerifyTokenEntity extends GenericEntity{
    @Column(name = "email", unique = true)
    String email;
    @Column(name = "value")
    String value;
}
