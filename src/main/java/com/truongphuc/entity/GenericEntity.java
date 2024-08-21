package com.truongphuc.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
@Data
@MappedSuperclass
public class GenericEntity {
    @Column(name = "id")
    @Id
    @GenericGenerator(name = "id_generator", strategy = "uuid")
    @GeneratedValue(generator = "id_generator")
    String id;

    
    @Column (name = "created_at")
    @CreationTimestamp
    LocalDateTime createdAt;

    @Column (name = "updated_at")
    @UpdateTimestamp
    LocalDateTime updatedAt;
}
