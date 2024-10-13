package com.truongphuc.repository;

import java.util.List;
import java.util.Optional;

import com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph;
import com.truongphuc.entity.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<UserEntity,String>{
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findUserByEmail(String email, EntityGraph entityGraph);
    Optional<UserEntity> findUserById(String id, EntityGraph entityGraph);

    @Query(value =
            "SELECT u from user u where u.name like :key% or substring(u.email, 1, locate('@', u.email) - 1) like :key% and u.active = true "
    )
    List<UserEntity> findAllByKey (@Param("key") String key);
}
