package com.truongphuc.repository;

import com.truongphuc.entity.ConversationEntity;
import com.truongphuc.entity.VerifyTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerifyTokenRepository  extends JpaRepository<VerifyTokenEntity, String> {
    Optional<VerifyTokenEntity> findByValue(String value);
    Optional<VerifyTokenEntity> findByEmail(String email);
}
