package com.truongphuc.mapper;

import com.truongphuc.dto.request.ConversationCreationRequest;
import com.truongphuc.dto.response.ConversationResponse;
import com.truongphuc.entity.ConversationEntity;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper (componentModel = "spring")
public interface ConversationMapper {
    ConversationEntity toConversationEntity(ConversationCreationRequest conversationCreationRequest);
    ConversationResponse toConversationResponse(ConversationEntity conversationEntity);
    List<ConversationResponse> toConversationResponseList(List<ConversationEntity> conversationEntities);
}