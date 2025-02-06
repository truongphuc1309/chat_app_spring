package com.truongphuc.service;

import com.truongphuc.dto.request.conversation.AddMemberToConversationRequest;
import com.truongphuc.dto.request.conversation.ConversationAvatarChangeRequest;
import com.truongphuc.dto.request.conversation.ConversationCreationRequest;
import com.truongphuc.dto.request.conversation.RemoveFromConversationRequest;
import com.truongphuc.dto.request.conversation.RenameConversationRequest;
import com.truongphuc.dto.response.PageResponse;
import com.truongphuc.dto.response.conversation.ConversationAvatarChangeResponse;
import com.truongphuc.dto.response.conversation.ConversationDetailsResponse;
import com.truongphuc.dto.response.conversation.RenameConversationResponse;

import org.springframework.stereotype.Service;

@Service
public interface ConversationService {
    ConversationDetailsResponse createConversation (String userEmail, ConversationCreationRequest conversationCreationRequest);

    ConversationDetailsResponse getConversationById (String userEmail, String conversationId);

    ConversationDetailsResponse getSingleConversationByUser(String yourEmail, String restUserId);

    PageResponse<ConversationDetailsResponse> getAllConversationsOfUser(String userEmail, int page, int pageSize);

    ConversationDetailsResponse addMemberToConversation(String adminEmail, AddMemberToConversationRequest addMemberToConversationRequest);

    RenameConversationResponse renameConversation(String adminEmail, RenameConversationRequest renameConversationRequest);

    ConversationAvatarChangeResponse changeAvatarConversation(String adminEmail, ConversationAvatarChangeRequest changeAvatarRequest) throws Exception;

    void removeAvatarConversation(String adminEmail, String conversationId) throws Exception;

    boolean removeFromConversation(String userEmail, RemoveFromConversationRequest request);

    boolean deleteConversation(String userEmail, String conversationId);
}
