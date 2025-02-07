package com.truongphuc.repository;

import java.util.Optional;

import com.truongphuc.entity.VerifyTokenEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VerifyTokenRepository  extends JpaRepository<VerifyTokenEntity, String> {
    Optional<VerifyTokenEntity> findByValue(String value);
    Optional<VerifyTokenEntity> findByEmail(String email);
}
