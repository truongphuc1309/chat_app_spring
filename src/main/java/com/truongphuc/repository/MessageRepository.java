package com.truongphuc.repository;

import com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph;
import com.truongphuc.entity.ConversationEntity;
import com.truongphuc.entity.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, String> {
    Optional<MessageEntity> findMessageById(String id, EntityGraph entityGraph);
    Page<MessageEntity> findAllByConversation(ConversationEntity conversation, Pageable pageable);

    @Query ("select m from  message m where  m.conversation.id = :conversationId and  m.createdAt = (select max (m1.createdAt) from message m1 where m1.conversation.id =:conversationId)")
    Optional<MessageEntity> getLastMessageOfConversation (@Param("conversationId") String conversationId);
}
