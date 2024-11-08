package com.truongphuc.repository;

import com.truongphuc.entity.ResetPasswordTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordTokenEntity, String> {
    Optional<ResetPasswordTokenEntity> findByEmail(String email);
    Optional<ResetPasswordTokenEntity> findByValue(String value);
}
