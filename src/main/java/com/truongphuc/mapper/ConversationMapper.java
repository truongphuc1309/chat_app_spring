package com.truongphuc.mapper;

import com.truongphuc.dto.request.ConversationCreationRequest;
import com.truongphuc.dto.response.ConversationDetailsResponse;
import com.truongphuc.entity.ConversationEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper (componentModel = "spring")
public interface ConversationMapper {
    ConversationEntity toConversationEntity(ConversationCreationRequest conversationCreationRequest);
    ConversationDetailsResponse toConversationResponse(ConversationEntity conversationEntity);
    List<ConversationDetailsResponse> toConversationResponseList(List<ConversationEntity> conversationEntities);
}