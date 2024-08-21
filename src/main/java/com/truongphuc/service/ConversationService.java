package com.truongphuc.service;

import com.truongphuc.dto.request.AddMemberToConversationRequest;
import com.truongphuc.dto.request.ConversationCreationRequest;
import com.truongphuc.dto.request.RemoveFromConversationRequest;
import com.truongphuc.dto.request.RenameConversationRequest;
import com.truongphuc.dto.response.ConversationDetailsResponse;
import com.truongphuc.dto.response.PageResponse;
import com.truongphuc.dto.response.RenameConversationResponse;
import org.springframework.stereotype.Service;

@Service
public interface ConversationService {
    ConversationDetailsResponse createConversation (String userEmail, ConversationCreationRequest conversationCreationRequest);
    ConversationDetailsResponse getConversationById (String userEmail, String conversationId);
    PageResponse<ConversationDetailsResponse> getAllConversationsOfUser(String userEmail, int page, int pageSize);
    ConversationDetailsResponse addMemberToConversation(String adminEmail, AddMemberToConversationRequest addMemberToConversationRequest);
    RenameConversationResponse renameConversation(String adminEmail, RenameConversationRequest renameConversationRequest);
    boolean removeFromConversation(String userEmail, RemoveFromConversationRequest request);
    boolean deleteConversation(String userEmail, String conversationId);
}
