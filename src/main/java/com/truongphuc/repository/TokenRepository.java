package com.truongphuc.repository;

import com.truongphuc.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, String> {
    Optional<TokenEntity> findByEmail(String email);

    @Query (value = "select t from token t where t.email = :email and (t.accessToken = :token or t.refreshToken = :token)")
    Optional<TokenEntity> findByEmailAndToken(@Param("email") String email, @Param("token") String token);
}
