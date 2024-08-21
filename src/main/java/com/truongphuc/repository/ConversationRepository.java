package com.truongphuc.repository;

import com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph;
import com.cosium.spring.data.jpa.entity.graph.domain2.NamedEntityGraph;
import com.truongphuc.entity.ConversationEntity;
import com.truongphuc.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ConversationRepository extends JpaRepository<ConversationEntity, String> {
    Optional<ConversationEntity> findConversationById(String id, EntityGraph entityGraph);
    Page<ConversationEntity>  findAllByMembers(Set<UserEntity> members, Pageable pageable);

   default Page<ConversationEntity> findAllByMember (UserEntity member, Pageable pageable){
       Set<UserEntity> memberHolder = new HashSet<>();
       memberHolder.add(member);
       Page<ConversationEntity> conversationPages = findAllByMembers(memberHolder, pageable);
       conversationPages.getContent().forEach(e -> {
           Optional<ConversationEntity> foundConversation = findConversationById(e.getId(), NamedEntityGraph.fetching("conversation-with-members"));
           if (foundConversation.isPresent()) {
               e.setMembers(foundConversation.get().getMembers());
               e.setCreatedBy(foundConversation.get().getCreatedBy());
           }
       });

       return conversationPages;
   }
}
