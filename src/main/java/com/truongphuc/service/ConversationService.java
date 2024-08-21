package com.truongphuc.service;

import com.truongphuc.dto.request.AddMemberToConversationRequest;
import com.truongphuc.dto.request.ConversationCreationRequest;
import com.truongphuc.dto.request.RemoveFromConversationRequest;
import com.truongphuc.dto.request.RenameConversationRequest;
import com.truongphuc.dto.response.ConversationResponse;
import com.truongphuc.dto.response.PageResponse;
import com.truongphuc.dto.response.RenameConversationResponse;
import org.springframework.stereotype.Service;

@Service
public interface ConversationService {
    ConversationResponse createConversation (String userEmail, ConversationCreationRequest conversationCreationRequest);
    ConversationResponse getConversationById (String userEmail, String conversationId);
    PageResponse<ConversationResponse> getAllConversationsOfUser(String userEmail, int page, int pageSize);
    ConversationResponse addMemberToConversation(String adminEmail, AddMemberToConversationRequest addMemberToConversationRequest);
    RenameConversationResponse renameConversation(String adminEmail, RenameConversationRequest renameConversationRequest);
    boolean removeFromConversation(String userEmail, RemoveFromConversationRequest request);
}
