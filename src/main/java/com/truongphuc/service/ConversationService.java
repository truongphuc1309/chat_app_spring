package com.truongphuc.service;

import com.truongphuc.dto.request.*;
import com.truongphuc.dto.response.ConversationAvatarChangeResponse;
import com.truongphuc.dto.response.ConversationDetailsResponse;
import com.truongphuc.dto.response.PageResponse;
import com.truongphuc.dto.response.RenameConversationResponse;
import org.springframework.stereotype.Service;

@Service
public interface ConversationService {
    ConversationDetailsResponse createConversation (String userEmail, ConversationCreationRequest conversationCreationRequest);
    ConversationDetailsResponse getConversationById (String userEmail, String conversationId);
    ConversationDetailsResponse getSingleConversationByUser(String yourEmail, String restUserId);
    PageResponse<ConversationDetailsResponse> getAllConversationsOfUser(String userEmail, int page, int pageSize);
    ConversationDetailsResponse addMemberToConversation(String adminEmail, AddMemberToConversationRequest addMemberToConversationRequest);
    RenameConversationResponse renameConversation(String adminEmail, RenameConversationRequest renameConversationRequest);
    ConversationAvatarChangeResponse changeAvatarConversation(String adminEmail, ConversationAvatarChangeRequest changeAvatarRequest);
    boolean removeFromConversation(String userEmail, RemoveFromConversationRequest request);
    boolean deleteConversation(String userEmail, String conversationId);
}
