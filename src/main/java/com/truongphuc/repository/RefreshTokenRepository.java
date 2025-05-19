package com.truongphuc.repository;

import com.truongphuc.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, String> {
    Optional<RefreshTokenEntity> findByEmail(String email);

    Optional<RefreshTokenEntity> findByValue(String value);

    @Query("select rt from refreshToken rt where  rt.expiredAt < now()")
    List<RefreshTokenEntity> findAllExpiredToken();
}