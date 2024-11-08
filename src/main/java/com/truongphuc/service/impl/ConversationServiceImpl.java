package com.truongphuc.service.impl;

import com.cosium.spring.data.jpa.entity.graph.domain2.NamedEntityGraph;
import com.truongphuc.constant.ExceptionCode;
import com.truongphuc.dto.request.conversation.*;
import com.truongphuc.dto.response.conversation.ConversationAvatarChangeResponse;
import com.truongphuc.dto.response.conversation.ConversationDetailsResponse;
import com.truongphuc.dto.response.PageResponse;
import com.truongphuc.dto.response.conversation.RenameConversationResponse;
import com.truongphuc.entity.ConversationEntity;
import com.truongphuc.entity.FileUploadEntity;
import com.truongphuc.entity.UserEntity;
import com.truongphuc.exception.AppException;
import com.truongphuc.mapper.ConversationMapper;
import com.truongphuc.repository.ConversationRepository;
import com.truongphuc.repository.UserRepository;
import com.truongphuc.service.CloudinaryService;
import com.truongphuc.service.ConversationService;
import com.truongphuc.service.FileUploadService;
import com.truongphuc.util.ConversationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

    @Override
    public ConversationDetailsResponse createConversation(String userEmail, ConversationCreationRequest conversationCreationRequest) {
        UserEntity foundUser = userRepository.findByEmail(userEmail).get();
        ConversationEntity newConversation = conversationMapper.toConversationEntity(conversationCreationRequest);

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

            Optional<ConversationEntity> foundConversation = conversationRepository.findSingleConversationByMembers(firstMember, secondMember);

            if (foundConversation.isPresent())
                throw new AppException("Existed Conversation", ExceptionCode.EXISTED_CONVERSATION);
        }

        newConversation.setMembers(newMembers);
        conversationRepository.save(newConversation);

        return conversationMapper.toConversationResponse(newConversation);
    }

    @Override
    public ConversationDetailsResponse getConversationById(String userEmail, String conversationId) {
        Optional<UserEntity> foundUser = userRepository.findByEmail(userEmail);

        Optional<ConversationEntity> foundConversation = conversationRepository.findConversationById(conversationId, NamedEntityGraph.fetching("conversation-with-members"));

        if (foundConversation.isEmpty())
            throw new AppException("Invalid conversation", ExceptionCode.NON_EXISTED_CONVERSATION);

        Set<UserEntity> members = foundConversation.get().getMembers();
        if (!members.contains(foundUser.get()))
            throw new AppException("Can not access to another user's conversation", ExceptionCode.INVALID_MEMBER);

        return conversationMapper.toConversationResponse(foundConversation.get());
    }

    @Override
    public ConversationDetailsResponse getSingleConversationByUser(String yourEmail, String restUserId) {
        Optional<UserEntity> foundUser = userRepository.findByEmail(yourEmail);

        Optional<UserEntity> restUser = userRepository.findById(restUserId);
        if (foundUser.isEmpty() || restUser.isEmpty())
            throw new AppException("Invalid user", ExceptionCode.NON_EXISTED_USER);

        Optional<ConversationEntity> foundConversation = conversationRepository.findSingleConversationByMembers(foundUser.get(), restUser.get());

        if (foundConversation.isEmpty())
            throw new AppException("Invalid conversation", ExceptionCode.NON_EXISTED_CONVERSATION);

        return conversationMapper.toConversationResponse(foundConversation.get());
    }

    @Override
    public PageResponse<ConversationDetailsResponse> getAllConversationsOfUser(String userEmail, int page, int pageSize) {
        if (page <= 0 || pageSize <= 0)
            throw new AppException("Invalid argument", ExceptionCode.INVALID_ARGUMENT);

        Optional<UserEntity> foundUser = userRepository.findUserByEmail(userEmail, NamedEntityGraph.fetching("user-with-conversations"));
        if (foundUser.isEmpty())
            throw new AppException("Invalid user", ExceptionCode.NON_EXISTED_USER);

        Sort sorter = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page - 1, pageSize, sorter);
        Page<ConversationEntity> conversationPage = conversationRepository.findAllByMember(foundUser.get(), pageable);

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
        Optional<ConversationEntity> foundConversation = conversationRepository.findConversationById(addMemberToConversationRequest.getConversationId(), NamedEntityGraph.fetching("conversation-with-members"));
        if (foundConversation.isEmpty())
            throw new AppException("Invalid conversation", ExceptionCode.NON_EXISTED_CONVERSATION);

        if (!conversationUtil.isAdminOfConversation(adminUser.get(), foundConversation.get()))
            throw new AppException("Forbidden to access", ExceptionCode.INVALID_ROLE);

        Optional<UserEntity> foundUser = userRepository.findById(addMemberToConversationRequest.getUserId());
        if (foundUser.isEmpty())
            throw new AppException("Invalid user", ExceptionCode.NON_EXISTED_USER);

        foundConversation.get().getMembers().add(foundUser.get());

        ConversationEntity result = conversationRepository.save(foundConversation.get());

        return conversationMapper.toConversationResponse(result);
    }

    @Override
    public RenameConversationResponse renameConversation(String adminEmail, RenameConversationRequest renameConversationRequest) {
        Optional<UserEntity> adminUser = userRepository.findByEmail(adminEmail);
        Optional<ConversationEntity> foundConversation = conversationRepository.findConversationById(renameConversationRequest.getConversationId(), NamedEntityGraph.fetching("conversation-with-createdBy"));

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
        Optional<ConversationEntity> foundConversation = conversationRepository.findConversationById(changeAvatarRequest.getConversationId(), NamedEntityGraph.fetching("conversation-with-createdBy"));

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
        Optional<ConversationEntity> foundConversation = conversationRepository.findConversationById(conversationId, NamedEntityGraph.fetching("conversation-with-createdBy"));


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
        Optional<ConversationEntity> foundConversation = conversationRepository.findConversationById(request.getConversationId(), NamedEntityGraph.fetching("conversation-with-members"));
        if (foundConversation.isEmpty())
            throw new AppException("Invalid conversation", ExceptionCode.NON_EXISTED_CONVERSATION);

        Optional<UserEntity> foundMember = userRepository.findById(request.getMemberId());
        if (foundMember.isEmpty())
            throw new AppException("Invalid user", ExceptionCode.INVALID_MEMBER);

        if (!foundMember.get().equals(foundUser.get()) && !conversationUtil.isAdminOfConversation(foundUser.get(), foundConversation.get()))
            throw new AppException("Forbidden to access", ExceptionCode.INVALID_ROLE);

        if (!foundConversation.get().getMembers().contains(foundMember.get()))
            throw new AppException("Invalid access", ExceptionCode.INVALID_ROLE);

        foundConversation.get().getMembers().remove(foundMember.get());
        conversationRepository.save(foundConversation.get());

        return true;
    }

    @Override
    public boolean deleteConversation(String userEmail, String conversationId) {
        Optional<UserEntity> foundUser = userRepository.findByEmail(userEmail);
        Optional<ConversationEntity> foundConversation = conversationRepository.findConversationById(conversationId, NamedEntityGraph.fetching("conversation-with-createdBy"));

        if (foundConversation.isEmpty())
            throw new AppException("Invalid conversation", ExceptionCode.NON_EXISTED_CONVERSATION);

        if (!conversationUtil.isAdminOfConversation(foundUser.get(), foundConversation.get()))
            throw new AppException("Forbidden to access", ExceptionCode.INVALID_ROLE);

        conversationRepository.deleteById(conversationId);

        return true;
    }
}