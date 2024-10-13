package com.truongphuc.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph;
import com.cosium.spring.data.jpa.entity.graph.domain2.NamedEntityGraph;
import com.truongphuc.entity.ConversationEntity;
import com.truongphuc.entity.UserEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends JpaRepository<ConversationEntity, String> {
    Optional<ConversationEntity> findConversationById(String id, EntityGraph entityGraph);
    Page<ConversationEntity>  findAllByMembers(Set<UserEntity> members, Pageable pageable);
    List<ConversationEntity> findAllByMembersAndIsGroup(Set<UserEntity> members, boolean isGroup);

    default Optional<ConversationEntity> findSingleConversationByMembers(UserEntity firstMember, UserEntity secondMember){
        Set<UserEntity> members = new HashSet<>();
        members.add(firstMember);
        List<ConversationEntity> conversations = findAllByMembersAndIsGroup(members, false);

        return conversations.stream().filter(e -> {
            Optional<ConversationEntity> foundConversation = findConversationById(e.getId(), NamedEntityGraph.fetching("conversation-with-members"));
            if (foundConversation.isPresent() && foundConversation.get().getMembers().contains(secondMember)) {
                e.setMembers(foundConversation.get().getMembers());
                e.setCreatedBy(foundConversation.get().getCreatedBy());
                return true;
            }
            return false;
        }).findFirst();
    }

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
