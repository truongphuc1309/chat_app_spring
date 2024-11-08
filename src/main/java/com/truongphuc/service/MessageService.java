package com.truongphuc.service;

import com.truongphuc.dto.request.message.MessageRequest;
import com.truongphuc.dto.response.message.MessageDetailsResponse;
import com.truongphuc.dto.response.message.MessageResponse;
import com.truongphuc.dto.response.PageResponse;

import java.io.IOException;

public interface MessageService {
    MessageDetailsResponse sendMessage(String userEmail, MessageRequest messageRequest) throws IOException;

    PageResponse<MessageResponse> getAllMessagesOfConversation(String userEmail, String conversationId, int page, int pageSize);

    MessageDetailsResponse getMessageById(String userEmail,String id);

    MessageResponse getLastMessageOfConversation(String userEmail, String conversationId);

    MessageDetailsResponse deleteMessage(String userEmail, String id) throws Exception;
}
