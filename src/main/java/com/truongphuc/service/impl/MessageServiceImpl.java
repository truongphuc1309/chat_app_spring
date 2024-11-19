package com.truongphuc.service.impl;

import com.truongphuc.constant.ExceptionCode;
import com.truongphuc.dto.FileUploadDto;
import com.truongphuc.dto.MemberDto;
import com.truongphuc.dto.request.message.MessageRequest;
import com.truongphuc.dto.response.PageResponse;
import com.truongphuc.dto.response.message.LastReadMessageResponse;
import com.truongphuc.dto.response.message.MessageDetailsResponse;
import com.truongphuc.dto.response.message.MessageResponse;
import com.truongphuc.entity.*;
import com.truongphuc.exception.AppException;
import com.truongphuc.mapper.FileUploadMapper;
import com.truongphuc.mapper.MessageMapper;
import com.truongphuc.mapper.UserMapper;
import com.truongphuc.repository.*;
import com.truongphuc.service.FileUploadService;
import com.truongphuc.service.MessageService;
import com.truongphuc.util.ConversationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class MessageServiceImpl implements MessageService {
    String ALLOWED_TYPE[] = {"image", "video", "file", "text", "audio", "voice"};
    UserRepository userRepository;
    UserMapper userMapper;
    ConversationRepository conversationRepository;
    ConversationUtil conversationUtil;
    MessageRepository messageRepository;
    CustomizedMessageRepository customizedMessageRepository;
    MessageMapper messageMapper;
    FileUploadService fileUploadService;
    FileUploadMapper fileUploadMapper;
    ParticipantRepository participantRepository;

    @Override
    public MessageDetailsResponse sendMessage(String userEmail, MessageRequest messageRequest) throws IOException {
        Optional<UserEntity> foundUser = userRepository.findByEmail(userEmail);

        if (foundUser.isEmpty())
            throw new AppException("Invalid user", ExceptionCode.INACTIVE_USER);

        Optional<ConversationEntity> foundConversation = conversationRepository.findById(messageRequest.getConversationId());

        if (foundConversation.isEmpty())
            throw new AppException("Invalid conversation", ExceptionCode.NON_EXISTED_CONVERSATION);

        Set<UserEntity> members = conversationUtil.getMembersOfConversation(foundConversation.get());

        if (!members.contains(foundUser.get()))
            throw new AppException("Forbidden to access", ExceptionCode.INVALID_ROLE);

        // Get message's seq
        long totalMessagesOfConversation = customizedMessageRepository.getTotalMessagesByConversationId(foundConversation.get().getId());
        long seq = totalMessagesOfConversation + 1;

        MessageEntity newMessage = MessageEntity.builder()
                .type(messageRequest.getType())
                .conversation(foundConversation.get())
                .user(foundUser.get())
                .active(true)
                .seq(seq)
                .build();


        if (!Arrays.asList(ALLOWED_TYPE).contains(messageRequest.getType()))
            throw new AppException("Invalid message type", ExceptionCode.INVALID_ARGUMENT);

        // Check whether message is text or a file
        if(messageRequest.getType().equals("text")){
            newMessage.setContent(messageRequest.getContent());
        }
        else{
            if (messageRequest.getFile() == null)
                throw new AppException("Required a attached file", ExceptionCode.INVALID_ARGUMENT);
            FileUploadDto uploadedFileDto = fileUploadService.uploadFileMessage(messageRequest.getFile());
            FileUploadEntity newUploadedFile = fileUploadMapper.toFileUploadEntity(uploadedFileDto);
            newMessage.setFile(newUploadedFile);
            newMessage.setContent(null);
        }


        MessageEntity result = messageRepository.save(newMessage);
        foundConversation.get().setUpdatedAt(result.getCreatedAt());
        conversationRepository.save(foundConversation.get());

        return messageMapper.toMessageDetailsResponse(result);
    }

    @Override
    public PageResponse<MessageResponse> getAllMessagesOfConversation(String userEmail, String conversationId, int page, int pageSize) {
        Optional<UserEntity> foundUser = userRepository.findByEmail(userEmail);
        Optional<ConversationEntity> foundConversation = conversationRepository.findById(conversationId);

        if (foundConversation.isEmpty())
            throw new AppException("Invalid conversation", ExceptionCode.NON_EXISTED_CONVERSATION);

        Set<UserEntity> members = conversationUtil.getMembersOfConversation(foundConversation.get());


        if (!members.contains(foundUser.get()))
            throw new AppException("Forbidden to access", ExceptionCode.INVALID_ROLE);

        Sort sorter = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, pageSize, sorter);

        Page<MessageEntity> result = messageRepository.findAllByConversation(foundConversation.get(), pageable);

        return PageResponse.<MessageResponse>builder()
                .currentPage(result.getNumber() + 1)
                .pageSize(result.getSize())
                .totalPages(result.getTotalPages())
                .numberOfElements(result.getNumberOfElements())
                .totalElements(result.getTotalElements())
                .content(messageMapper.toMessageResponseList(result.getContent()))
                .build();

    }

    @Override
    public MessageDetailsResponse getMessageById(String userEmail, String id) {
        Optional<UserEntity> foundUser = userRepository.findByEmail(userEmail);
        if (foundUser.isEmpty())
            throw new AppException("Invalid user", ExceptionCode.INACTIVE_USER);

        Optional<MessageEntity> foundMessage = messageRepository.findById(id);
        if (foundMessage.isEmpty())
            throw new AppException("Invalid message", ExceptionCode.NON_EXISTED_MESSAGE);

        ConversationEntity conversation = foundMessage.get().getConversation();
        Set<UserEntity> members = conversationUtil.getMembersOfConversation(conversation);

        if (!members.contains(foundUser.get()))
            throw new AppException("Forbidden to access", ExceptionCode.INVALID_ROLE);

        return messageMapper.toMessageDetailsResponse(foundMessage.get());
    }

    @Override
    public MessageResponse getLastMessageOfConversation(String userEmail, String conversationId) {
        Optional<UserEntity> foundUser = userRepository.findByEmail(userEmail);
        if (foundUser.isEmpty())
            throw new AppException("Invalid user", ExceptionCode.INACTIVE_USER);

        Optional<ConversationEntity> foundConversation = conversationRepository.findById(conversationId);

        if (foundConversation.isEmpty())
            throw new AppException("Invalid conversation", ExceptionCode.NON_EXISTED_CONVERSATION);

        Set<UserEntity> members = conversationUtil.getMembersOfConversation(foundConversation.get());

        // Check whether user in conversation or not
        if (!members.contains(foundUser.get()))
            throw new AppException("Forbidden to access", ExceptionCode.INVALID_ROLE);

        Optional<MessageEntity> lastMessage = messageRepository.getLastMessageOfConversation(conversationId);

        return lastMessage.map(messageMapper::toMessageResponse).orElse(null);
    }

    @Override
    public MessageDetailsResponse deleteMessage(String userEmail, String id) throws Exception {
        Optional<MessageEntity> foundMessage = messageRepository.findById(id);
        if (foundMessage.isEmpty())
            throw new AppException("Invalid message", ExceptionCode.NON_EXISTED_MESSAGE);

        Optional<UserEntity> foundUser = userRepository.findByEmail(userEmail);

        if (!foundMessage.get().getUser().equals(foundUser.get()))
            throw new AppException("Forbidden to access", ExceptionCode.INVALID_ROLE);

        FileUploadEntity oldFile = foundMessage.get().getFile();

        foundMessage.get().setActive(false);
        foundMessage.get().setContent(null);
        foundMessage.get().setFile(null);

        messageRepository.save(foundMessage.get());

        if (oldFile != null) fileUploadService.deleteFile(oldFile);

        return messageMapper.toMessageDetailsResponse(foundMessage.get());
    }

    @Override
    public LastReadMessageResponse readLastMessageOfConversation(String userEmail, String lastMessageId) {
        Optional<UserEntity> foundUser = userRepository.findByEmail(userEmail);
        if (foundUser.isEmpty())
            throw new AppException("Invalid user", ExceptionCode.INACTIVE_USER);

        Optional<MessageEntity> foundMessage = messageRepository.findById(lastMessageId);
        if (foundMessage.isEmpty())
            throw new AppException("Invalid message", ExceptionCode.NON_EXISTED_MESSAGE);

        Optional<ParticipantEntity> participant = participantRepository.findByUserAndConversation(foundUser.get(), foundMessage.get().getConversation());

        if (participant.isEmpty())
            throw new AppException("Participant not found", ExceptionCode.INVALID_ARGUMENT);

        participant.get().setLastRead(foundMessage.get().getSeq());

        participantRepository.save(participant.get());

        MemberDto memberDto = userMapper.toMemberDto(foundUser.get());
        memberDto.setLastRead(participant.get().getLastRead());

        return LastReadMessageResponse.builder()
                .id(lastMessageId)
                .reader(memberDto)
                .build();
    }
}
