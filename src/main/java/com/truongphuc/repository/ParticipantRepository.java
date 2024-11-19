package com.truongphuc.repository;

import com.truongphuc.entity.ConversationEntity;
import com.truongphuc.entity.ParticipantEntity;
import com.truongphuc.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ParticipantRepository extends JpaRepository<ParticipantEntity, String> {
    List<ParticipantEntity> findByUser(UserEntity user);

    Optional<ParticipantEntity> findByUserAndConversation(UserEntity user, ConversationEntity conversation);

    Set<ParticipantEntity> findByConversation(ConversationEntity conversation);

    @Transactional
    void deleteAllByConversation(ConversationEntity conversation);
}
