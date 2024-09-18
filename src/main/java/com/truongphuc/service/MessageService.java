package com.truongphuc.service;

import com.truongphuc.dto.request.MessageRequest;
import com.truongphuc.dto.response.MessageDetailsResponse;
import com.truongphuc.dto.response.MessageResponse;
import com.truongphuc.dto.response.PageResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MessageService {
    MessageDetailsResponse sendMessage(String userEmail, MessageRequest messageRequest);

    PageResponse<MessageResponse> getAllMessagesOfConversation(String userEmail, String conversationId, int page, int pageSize);

    MessageDetailsResponse getMessageById(String userEmail,String id);

    MessageResponse getLastMessageOfConversation(String userEmail, String conversationId);

    MessageDetailsResponse deleteMessage(String userEmail, String id);
}
