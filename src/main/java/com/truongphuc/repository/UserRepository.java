package com.truongphuc.repository;

import java.util.Optional;

import com.truongphuc.entity.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<UserEntity,String>{
    Optional<UserEntity> findByEmail (String email);
}
