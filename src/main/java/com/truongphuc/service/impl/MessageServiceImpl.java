package com.truongphuc.service.impl;

import com.cosium.spring.data.jpa.entity.graph.domain2.NamedEntityGraph;
import com.truongphuc.constant.ExceptionCode;
import com.truongphuc.dto.request.message.MessageRequest;
import com.truongphuc.dto.response.message.MessageDetailsResponse;
import com.truongphuc.dto.response.message.MessageResponse;
import com.truongphuc.dto.response.PageResponse;
import com.truongphuc.entity.ConversationEntity;
import com.truongphuc.entity.FileUploadEntity;
import com.truongphuc.entity.MessageEntity;
import com.truongphuc.entity.UserEntity;
import com.truongphuc.exception.AppException;
import com.truongphuc.mapper.MessageMapper;
import com.truongphuc.repository.ConversationRepository;
import com.truongphuc.repository.MessageRepository;
import com.truongphuc.repository.UserRepository;
import com.truongphuc.service.FileUploadService;
import com.truongphuc.service.MessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class MessageServiceImpl implements MessageService {
    UserRepository userRepository;
    ConversationRepository conversationRepository;
    MessageRepository messageRepository;
    MessageMapper messageMapper;
    FileUploadService fileUploadService;

    @Override
    public MessageDetailsResponse sendMessage(String userEmail, MessageRequest messageRequest) throws IOException {
        Optional<UserEntity> foundUser = userRepository.findByEmail(userEmail);
        Optional<ConversationEntity> foundConversation = conversationRepository.findConversationById(messageRequest.getConversationId(), NamedEntityGraph.fetching("conversation-with-members"));

        if (foundConversation.isEmpty())
            throw new AppException("Invalid conversation", ExceptionCode.NON_EXISTED_CONVERSATION);

        if (!foundConversation.get().getMembers().contains(foundUser.get()))
            throw new AppException("Forbidden to access", ExceptionCode.INVALID_ROLE);

        MessageEntity newMessage = MessageEntity.builder()
                .type("text")
                .content(messageRequest.getContent())
                .conversation(foundConversation.get())
                .user(foundUser.get())
                .active(true)
                .build();


        // Check if message was a file
        if (messageRequest.getFile() != null){
            FileUploadEntity newUploadedFile = fileUploadService.uploadFileMessage(messageRequest.getFile());
            newMessage.setType("file");
            newMessage.setFile(newUploadedFile);
        }


        MessageEntity result = messageRepository.save(newMessage);
        foundConversation.get().setUpdatedAt(result.getCreatedAt());
        conversationRepository.save(foundConversation.get());

        return messageMapper.toMessageDetailsResponse(result);
    }

    @Override
    public PageResponse<MessageResponse> getAllMessagesOfConversation(String userEmail, String conversationId, int page, int pageSize) {
        Optional<UserEntity> foundUser = userRepository.findByEmail(userEmail);
        Optional<ConversationEntity> foundConversation = conversationRepository.findConversationById(conversationId,  NamedEntityGraph.fetching("conversation-with-members"));

        if (foundConversation.isEmpty())
            throw new AppException("Invalid conversation", ExceptionCode.NON_EXISTED_CONVERSATION);

        if (!foundConversation.get().getMembers().contains(foundUser.get()))
            throw new AppException("Forbidden to access", ExceptionCode.INVALID_ROLE);

        Sort sorter = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, pageSize, sorter);

        Page<MessageEntity> result = messageRepository.findAllByConversation(foundConversation.get(), NamedEntityGraph.fetching("message-with-user"), pageable);

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
        Optional<MessageEntity> foundMessage = messageRepository.findMessageById(id, NamedEntityGraph.fetching("message-with-user-and-conversation"));
        if (foundMessage.isEmpty())
            throw new AppException("Invalid message", ExceptionCode.NON_EXISTED_MESSAGE);

        Optional<UserEntity> foundUser = userRepository.findByEmail(userEmail);
        if (!foundMessage.get().getConversation().getMembers().contains(foundUser.get()))
            throw new AppException("Forbidden to access", ExceptionCode.INVALID_ROLE);

        return messageMapper.toMessageDetailsResponse(foundMessage.get());
    }

    @Override
    public MessageResponse getLastMessageOfConversation(String userEmail, String conversationId) {
        Optional<UserEntity> foundUser = userRepository.findByEmail(userEmail);
        Optional<ConversationEntity> foundConversation = conversationRepository.findConversationById(conversationId,  NamedEntityGraph.fetching("conversation-with-members"));

        if (foundConversation.isEmpty())
            throw new AppException("Invalid conversation", ExceptionCode.NON_EXISTED_CONVERSATION);

        // Check whether user in conversation or not
        if (!foundConversation.get().getMembers().contains(foundUser.get()))
            throw new AppException("Forbidden to access", ExceptionCode.INVALID_ROLE);

        Optional<MessageEntity> lastMessage = messageRepository.getLastMessageOfConversation(conversationId, NamedEntityGraph.fetching("message-with-user"));

        if (lastMessage.isEmpty())
            throw new AppException("Conversation is empty", ExceptionCode.NON_MATCHED_MESSAGE);

        return messageMapper.toMessageResponse(lastMessage.get());
    }

    @Override
    public MessageDetailsResponse deleteMessage(String userEmail, String id) {
        Optional<MessageEntity> foundMessage = messageRepository.findMessageById(id, NamedEntityGraph.fetching("message-with-user-and-conversation"));
        if (foundMessage.isEmpty())
            throw new AppException("Invalid message", ExceptionCode.NON_EXISTED_MESSAGE);

        Optional<UserEntity> foundUser = userRepository.findByEmail(userEmail);

        if (!foundMessage.get().getUser().equals(foundUser.get()))
            throw new AppException("Forbidden to access", ExceptionCode.INVALID_ROLE);

        foundMessage.get().setActive(false);

        messageRepository.save(foundMessage.get());
        return messageMapper.toMessageDetailsResponse(foundMessage.get());
    }
}
