package com.truongphuc.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
@Getter
@Setter
@MappedSuperclass
public class GenericEntity {
    @Column(name = "id")
    @Id
    @GenericGenerator(name = "id_generator", strategy = "uuid")
    @GeneratedValue(generator = "id_generator")
    String id;

    
    @Column (name = "createdat")
    @CreationTimestamp
    LocalDateTime createdAt;

    @Column (name = "updatedat")
    @UpdateTimestamp
    LocalDateTime updatedAt;
}
