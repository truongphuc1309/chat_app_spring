package com.truongphuc.service.impl;

import com.cosium.spring.data.jpa.entity.graph.domain2.NamedEntityGraph;
import com.truongphuc.constant.ExceptionCode;
import com.truongphuc.dto.request.MessageRequest;
import com.truongphuc.dto.response.MessageDetailsResponse;
import com.truongphuc.dto.response.MessageResponse;
import com.truongphuc.dto.response.PageResponse;
import com.truongphuc.entity.ConversationEntity;
import com.truongphuc.entity.MessageEntity;
import com.truongphuc.entity.UserEntity;
import com.truongphuc.exception.AppException;
import com.truongphuc.mapper.MessageMapper;
import com.truongphuc.repository.ConversationRepository;
import com.truongphuc.repository.MessageRepository;
import com.truongphuc.repository.UserRepository;
import com.truongphuc.service.MessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class MessageServiceImpl implements MessageService {
    UserRepository userRepository;
    ConversationRepository conversationRepository;
    MessageRepository messageRepository;
    MessageMapper messageMapper;

    @Override
    public MessageDetailsResponse sendMessage(String userEmail, MessageRequest messageRequest) {
        Optional<UserEntity> foundUser = userRepository.findByEmail(userEmail);
        Optional<ConversationEntity> foundConversation = conversationRepository.findConversationById(messageRequest.getConversationId(), NamedEntityGraph.fetching("conversation-with-members"));

        if (foundConversation.isEmpty())
            throw new AppException("Invalid conversation", ExceptionCode.NON_EXISTED_CONVERSATION);

        if (!foundConversation.get().getMembers().contains(foundUser.get()))
            throw new AppException("Forbidden to access", ExceptionCode.INVALID_ROLE);

        MessageEntity newMessage = MessageEntity.builder()
                .content(messageRequest.getContent())
                .conversation(foundConversation.get())
                .user(foundUser.get())
                .build();

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
                .totalElements(result.getNumberOfElements())
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
    public boolean deleteMessage(String userEmail, String id) {
        Optional<MessageEntity> foundMessage = messageRepository.findMessageById(id, NamedEntityGraph.fetching("message-with-user"));
        if (foundMessage.isEmpty())
            throw new AppException("Invalid message", ExceptionCode.NON_EXISTED_MESSAGE);

        Optional<UserEntity> foundUser = userRepository.findByEmail(userEmail);

        if (!foundMessage.get().getUser().equals(foundUser.get()))
            throw new AppException("Forbidden to access", ExceptionCode.INVALID_ROLE);

        messageRepository.delete(foundMessage.get());

        return true;
    }
}
