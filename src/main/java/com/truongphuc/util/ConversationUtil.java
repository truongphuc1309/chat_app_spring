package com.truongphuc.util;

import com.truongphuc.dto.ConversationDto;
import com.truongphuc.dto.MemberDto;
import com.truongphuc.entity.ConversationEntity;
import com.truongphuc.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ConversationUtil {
    boolean isAdminOfConversation(UserEntity user, ConversationEntity conversation);

    List<ConversationEntity> getSingleConversationsOfUser(UserEntity user);

    Optional<ConversationEntity> getSingleConversation(UserEntity firstUser, UserEntity secondUser);

    Set<UserEntity> getMembersOfConversation(ConversationEntity conversation);

    Set<MemberDto> getMemberDtoListOfConversation(ConversationEntity conversation);

    Page<ConversationDto> getConversationsOfUser(UserEntity user, Pageable pageable);
}
