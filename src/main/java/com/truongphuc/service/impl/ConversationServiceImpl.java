package com.truongphuc.service.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.truongphuc.constant.ExceptionCode;
import com.truongphuc.dto.ConversationDto;
import com.truongphuc.dto.MemberDto;
import com.truongphuc.dto.request.conversation.AddMemberToConversationRequest;
import com.truongphuc.dto.request.conversation.ConversationAvatarChangeRequest;
import com.truongphuc.dto.request.conversation.ConversationCreationRequest;
import com.truongphuc.dto.request.conversation.RemoveFromConversationRequest;
import com.truongphuc.dto.request.conversation.RenameConversationRequest;
import com.truongphuc.dto.response.PageResponse;
import com.truongphuc.dto.response.conversation.ConversationAvatarChangeResponse;
import com.truongphuc.dto.response.conversation.ConversationDetailsResponse;
import com.truongphuc.dto.response.conversation.RenameConversationResponse;
import com.truongphuc.entity.ConversationEntity;
import com.truongphuc.entity.FileUploadEntity;
import com.truongphuc.entity.ParticipantEntity;
import com.truongphuc.entity.UserEntity;
import com.truongphuc.exception.AppException;
import com.truongphuc.mapper.ConversationMapper;
import com.truongphuc.repository.ConversationRepository;
import com.truongphuc.repository.ParticipantRepository;
import com.truongphuc.repository.UserRepository;
import com.truongphuc.service.ConversationService;
import com.truongphuc.service.FileUploadService;
import com.truongphuc.util.ConversationUtil;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ConversationServiceImpl implements ConversationService {
    UserRepository userRepository;
    ConversationRepository conversationRepository;
    ConversationMapper conversationMapper;
    ConversationUtil conversationUtil;
    FileUploadService fileUploadService;
    ParticipantRepository participantRepository;

    @Override
    public ConversationDetailsResponse createConversation(String userEmail, ConversationCreationRequest conversationCreationRequest) {
        UserEntity foundUser = userRepository.findByEmail(userEmail).get();
        ConversationEntity newConversation = conversationMapper.toConversationEntity(conversationCreationRequest);

        // Check if conversation is group
        if (conversationCreationRequest.isGroup())
        {
            if (conversationCreationRequest.getName() == null || conversationCreationRequest.getName().isEmpty())
                throw new AppException ("Name is required", ExceptionCode.INVALID_ARGUMENT);
            newConversation.setGroup(true);
        }
        else {
            newConversation.setGroup(false);
            newConversation.setName(null);
        }

        newConversation.setCreatedBy(foundUser);
        Set<UserEntity> newMembers = new HashSet<>();

        newMembers.add(foundUser);

        conversationCreationRequest.getAddedMembers().forEach(memberId -> {
            Optional<UserEntity> member = userRepository.findById(memberId);

            if (member.isEmpty())
                throw new AppException("Invalid member", ExceptionCode.NON_EXISTED_USER);

            newMembers.add(member.get());
        });

        if (!conversationCreationRequest.isGroup()){
            UserEntity firstMember = newMembers.toArray(new UserEntity[0])[0];
            UserEntity secondMember = newMembers.toArray(new UserEntity[0])[1];

            Optional<ConversationEntity> foundConversation = conversationUtil.getSingleConversation(firstMember, secondMember);

            if (foundConversation.isPresent() && !foundConversation.get().isGroup())
                throw new AppException("Existed Conversation", ExceptionCode.EXISTED_CONVERSATION);
        }


        var createdConversation = conversationRepository.save(newConversation);

        // Create all participant
        newMembers.forEach(member -> {
            ParticipantEntity newParticipant = ParticipantEntity.builder()
                    .conversation(createdConversation)
                    .user(member)
                    .build();
            participantRepository.save(newParticipant);
        });

        ConversationDetailsResponse response = conversationMapper.toConversationResponse(createdConversation);
        response.setGroup(createdConversation.isGroup());
        Set<MemberDto> memberDtoSet = conversationUtil.getMemberDtoListOfConversation(createdConversation);
        response.setMembers(memberDtoSet);

        return response;
    }

    @Override
    public ConversationDetailsResponse getConversationById(String userEmail, String conversationId) {
        Optional<UserEntity> foundUser = userRepository.findByEmail(userEmail);

        Optional<ConversationEntity> foundConversation = conversationRepository.findById(conversationId);

        if (foundConversation.isEmpty())
            throw new AppException("Invalid conversation", ExceptionCode.NON_EXISTED_CONVERSATION);

        Set<UserEntity> members = conversationUtil.getMembersOfConversation(foundConversation.get());
        if (!members.contains(foundUser.get()))
            throw new AppException("Can not access to another user's conversation", ExceptionCode.INVALID_MEMBER);

        ConversationDetailsResponse response = conversationMapper.toConversationResponse(foundConversation.get());
        Set<MemberDto> memberDtoSet = conversationUtil.getMemberDtoListOfConversation(foundConversation.get());

        response.setGroup(foundConversation.get().isGroup());
        response.setMembers(memberDtoSet);

        return response;
    }

    @Override
    public ConversationDetailsResponse getSingleConversationByUser(String yourEmail, String restUserId) {
        Optional<UserEntity> foundUser = userRepository.findByEmail(yourEmail);

        Optional<UserEntity> restUser = userRepository.findById(restUserId);
        if (foundUser.isEmpty() || restUser.isEmpty())
            throw new AppException("Invalid user", ExceptionCode.NON_EXISTED_USER);

        Optional<ConversationEntity> foundConversation = conversationUtil.getSingleConversation(foundUser.get(), restUser.get());

        if (foundConversation.isEmpty())
            throw new AppException("Invalid conversation", ExceptionCode.NON_EXISTED_CONVERSATION);

        var response = conversationMapper.toConversationResponse(foundConversation.get());
        response.setGroup(foundConversation.get().isGroup());

        return response;
    }

    @Override
    public PageResponse<ConversationDetailsResponse> getAllConversationsOfUser(String userEmail, int page, int pageSize) {
        if (page <= 0 || pageSize <= 0)
            throw new AppException("Invalid argument", ExceptionCode.INVALID_ARGUMENT);

        Optional<UserEntity> foundUser = userRepository.findByEmail(userEmail);
        if (foundUser.isEmpty())
            throw new AppException("Invalid user", ExceptionCode.NON_EXISTED_USER);

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<ConversationDto> conversationPage = conversationUtil.getConversationsOfUser(foundUser.get(),pageable);

        return PageResponse.<ConversationDetailsResponse>builder()
                .currentPage(conversationPage.getNumber() + 1)
                .pageSize(conversationPage.getSize())
                .totalPages(conversationPage.getTotalPages())
                .numberOfElements(conversationPage.getNumberOfElements())
                .totalElements(conversationPage.getTotalElements())
                .content(conversationMapper.toConversationResponseList(conversationPage.getContent()))
                .build();
    }

    @Override
    public ConversationDetailsResponse addMemberToConversation(String adminEmail, AddMemberToConversationRequest addMemberToConversationRequest) {
        Optional<UserEntity> adminUser = userRepository.findByEmail(adminEmail);
        Optional<ConversationEntity> foundConversation = conversationRepository.findById(addMemberToConversationRequest.getConversationId());

        if (foundConversation.isEmpty())
            throw new AppException("Invalid conversation", ExceptionCode.NON_EXISTED_CONVERSATION);

        Set<UserEntity> members = conversationUtil.getMembersOfConversation(foundConversation.get());


        if (!conversationUtil.isAdminOfConversation(adminUser.get(), foundConversation.get()))
            throw new AppException("Forbidden to access", ExceptionCode.INVALID_ROLE);

        Optional<UserEntity> foundUser = userRepository.findById(addMemberToConversationRequest.getUserId());
        if (foundUser.isEmpty())
            throw new AppException("Invalid user", ExceptionCode.NON_EXISTED_USER);

        if (members.contains(foundUser.get()))
            throw new AppException("Duplicate member", ExceptionCode.INVALID_MEMBER);

        // Add member
        ParticipantEntity newParticipant = ParticipantEntity.builder()
                .user(foundUser.get())
                .conversation(foundConversation.get())
                .build();

        participantRepository.save(newParticipant);

        ConversationEntity result = conversationRepository.save(foundConversation.get());


        // Get updated members
        Set<MemberDto> memberDtoSet = conversationUtil.getMemberDtoListOfConversation(result);


        ConversationDetailsResponse response =  conversationMapper.toConversationResponse(result);
        response.setMembers(memberDtoSet);
        response.setGroup(foundConversation.get().isGroup());

        return response;
    }

    @Override
    public RenameConversationResponse renameConversation(String adminEmail, RenameConversationRequest renameConversationRequest) {
        Optional<UserEntity> adminUser = userRepository.findByEmail(adminEmail);
        Optional<ConversationEntity> foundConversation = conversationRepository.findById(renameConversationRequest.getConversationId());

        if (adminUser.isEmpty())
            throw new AppException("Invalid user", ExceptionCode.NON_EXISTED_USER);

        if (foundConversation.isEmpty())
            throw new AppException("Invalid conversation", ExceptionCode.NON_EXISTED_CONVERSATION);


        if (!conversationUtil.isAdminOfConversation(adminUser.get(), foundConversation.get()))
            throw new AppException("Forbidden to access", ExceptionCode.INVALID_ROLE);

        String oldName = foundConversation.get().getName();

        foundConversation.get().setName(renameConversationRequest.getNewName());

        var result = conversationRepository.save(foundConversation.get());

        return RenameConversationResponse.builder()
                .conversationId(result.getId())
                .oldName(oldName)
                .newName(result.getName())
                .build();
    }

    @Override
    public ConversationAvatarChangeResponse changeAvatarConversation(String adminEmail, ConversationAvatarChangeRequest changeAvatarRequest) throws Exception {
        Optional<UserEntity> adminUser = userRepository.findByEmail(adminEmail);
        Optional<ConversationEntity> foundConversation = conversationRepository.findById(changeAvatarRequest.getConversationId());

        if (foundConversation.isEmpty() || !foundConversation.get().isGroup())
            throw new AppException("Invalid conversation", ExceptionCode.NON_EXISTED_CONVERSATION);

        if (!conversationUtil.isAdminOfConversation(adminUser.get(), foundConversation.get()))
            throw new AppException("Forbidden to access", ExceptionCode.INVALID_ROLE);

        FileUploadEntity oldAvatar = foundConversation.get().getAvatar();

        ConversationEntity updatedConversation = fileUploadService.uploadConversationAvatar(foundConversation.get(), changeAvatarRequest.getAvatar());

        var result = conversationRepository.save(updatedConversation);

        // Check and Delete old avatar
        if (oldAvatar != null) fileUploadService.deleteFile(oldAvatar);

        return ConversationAvatarChangeResponse.builder()
                .conversationId(result.getId())
                .avatar(result.getAvatar().getUrl())
                .build();
    }

    @Override
    public void removeAvatarConversation(String adminEmail,  String conversationId) throws Exception {
        Optional<UserEntity> adminUser = userRepository.findByEmail(adminEmail);
        Optional<ConversationEntity> foundConversation = conversationRepository.findById(conversationId);


        if (foundConversation.isEmpty() || !foundConversation.get().isGroup())
            throw new AppException("Invalid conversation", ExceptionCode.NON_EXISTED_CONVERSATION);

        if (!conversationUtil.isAdminOfConversation(adminUser.get(), foundConversation.get()))
            throw new AppException("Forbidden to access", ExceptionCode.INVALID_ROLE);

        FileUploadEntity oldAvatar = foundConversation.get().getAvatar();
        foundConversation.get().setAvatar(null);

        conversationRepository.save(foundConversation.get());

        // Check and Delete old avatar
        if (oldAvatar != null) fileUploadService.deleteFile(oldAvatar);
    }

    @Override
    public boolean removeFromConversation(String userEmail, RemoveFromConversationRequest request) {
        Optional<UserEntity> foundUser = userRepository.findByEmail(userEmail);
        Optional<ConversationEntity> foundConversation = conversationRepository.findById(request.getConversationId());

        if (foundConversation.isEmpty())
            throw new AppException("Invalid conversation", ExceptionCode.NON_EXISTED_CONVERSATION);

        Set<UserEntity> members = conversationUtil.getMembersOfConversation(foundConversation.get());


        Optional<UserEntity> foundMember = userRepository.findById(request.getMemberId());
        if (foundMember.isEmpty())
            throw new AppException("Invalid user", ExceptionCode.INVALID_MEMBER);

        if (!foundMember.get().equals(foundUser.get()) && !conversationUtil.isAdminOfConversation(foundUser.get(), foundConversation.get()))
            throw new AppException("Forbidden to access", ExceptionCode.INVALID_ROLE);

        if (!members.contains(foundMember.get()))
            throw new AppException("Invalid access", ExceptionCode.INVALID_ROLE);

        // Remove member
        Optional<ParticipantEntity> foundParticipant =  participantRepository.findByUserAndConversation(foundMember.get(), foundConversation.get());
        if (foundParticipant.isEmpty())
            throw new AppException("Invalid user", ExceptionCode.INVALID_MEMBER);
        participantRepository.delete(foundParticipant.get());

        return true;
    }

    @Override
    public boolean deleteConversation(String userEmail, String conversationId) {
        Optional<UserEntity> foundUser = userRepository.findByEmail(userEmail);
        Optional<ConversationEntity> foundConversation = conversationRepository.findById(conversationId);

        if (foundConversation.isEmpty())
            throw new AppException("Invalid conversation", ExceptionCode.NON_EXISTED_CONVERSATION);

        if (!conversationUtil.isAdminOfConversation(foundUser.get(), foundConversation.get()))
            throw new AppException("Forbidden to access", ExceptionCode.INVALID_ROLE);

        participantRepository.deleteAllByConversation(foundConversation.get());
        conversationRepository.deleteById(conversationId);

        return true;
    }
}
