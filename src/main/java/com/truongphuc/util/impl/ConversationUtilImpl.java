package com.truongphuc.util.impl;

import com.truongphuc.dto.ConversationDto;
import com.truongphuc.dto.MemberDto;
import com.truongphuc.entity.ConversationEntity;
import com.truongphuc.entity.ParticipantEntity;
import com.truongphuc.entity.UserEntity;
import com.truongphuc.mapper.ConversationMapper;
import com.truongphuc.mapper.UserMapper;
import com.truongphuc.repository.CustomizedConversationRepository;
import com.truongphuc.repository.ParticipantRepository;
import com.truongphuc.util.ConversationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ConversationUtilImpl implements ConversationUtil {
    final UserMapper userMapper;
    final private ParticipantRepository participantRepository;
    final private CustomizedConversationRepository customizedConversationRepository;
    final private ConversationMapper conversationMapper;

    @Override
    public boolean isAdminOfConversation(UserEntity userEntity, ConversationEntity conversation) {
        return conversation.getCreatedBy().equals(userEntity);
    }

    @Override
    public List<ConversationEntity> getSingleConversationsOfUser(UserEntity user) {
        List<ParticipantEntity> participants = participantRepository.findByUser(user);
        List<ConversationEntity> conversations = participants.stream().map(ParticipantEntity::getConversation).toList();
        return conversations.stream().filter(e -> !e.isGroup()).toList();
    }

    @Override
    public Optional<ConversationEntity> getSingleConversation(UserEntity firstUser, UserEntity secondUser) {

        List<ConversationEntity> singleConversationsOfFirstUser = getSingleConversationsOfUser(firstUser);
        List<ConversationEntity> singleConversationsOfSecondUser = getSingleConversationsOfUser(secondUser);

        return singleConversationsOfFirstUser.stream().filter(singleConversationsOfSecondUser::contains).findFirst();
    }

    @Override
    public Set<UserEntity> getMembersOfConversation(ConversationEntity conversation) {
        Set<ParticipantEntity> participantsOfConversation = participantRepository.findByConversation(conversation);

        List<UserEntity> membersOfConversation  = participantsOfConversation.stream().map(ParticipantEntity::getUser).toList();

        return new HashSet<>(membersOfConversation);
    }

    @Override
    public Set<MemberDto> getMemberDtoListOfConversation(ConversationEntity conversation) {
        Set<ParticipantEntity> participantsOfConversation = participantRepository.findByConversation(conversation);
        List<MemberDto> membersOfConversation  = participantsOfConversation.stream().map(e ->{
            MemberDto memberDto = userMapper.toMemberDto(e.getUser());
            memberDto.setLastRead(e.getLastRead());

            return memberDto;
        }).toList();

        return new HashSet<>(membersOfConversation);
    }

    @Override
    public Page<ConversationDto> getConversationsOfUser(UserEntity user, Pageable pageable) {
        List<ConversationEntity> conversations =  customizedConversationRepository.getAllConversationsByUserId(user.getId(), pageable);

        List<ConversationDto> conversationDtoList = conversations.stream().map(conversation -> {
            Set<MemberDto> members = getMemberDtoListOfConversation(conversation);
            ConversationDto conversationDto = conversationMapper.toConversationDto(conversation);
            conversationDto.setMembers(members);

            return conversationDto;
        }).toList();

        long total = customizedConversationRepository.getTotalConversationsByUserId(user.getId());

        return new PageImpl<>(conversationDtoList, pageable, total);
    };
}
