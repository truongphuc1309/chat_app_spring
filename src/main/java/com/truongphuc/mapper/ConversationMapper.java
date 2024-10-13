package com.truongphuc.mapper;

import com.truongphuc.dto.request.conversation.ConversationCreationRequest;
import com.truongphuc.dto.response.conversation.ConversationDetailsResponse;
import com.truongphuc.entity.ConversationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper (componentModel = "spring")
public interface ConversationMapper {
    ConversationEntity toConversationEntity(ConversationCreationRequest conversationCreationRequest);

    @Mapping(target = "avatar", source = "avatar.url")
    @Mapping(target = "createdBy.avatar", source = "createdBy.avatar.url")

    @Mapping(target = "members", source = "members", ignore = true)
    ConversationDetailsResponse toConversationResponse(ConversationEntity conversationEntity);
    List<ConversationDetailsResponse> toConversationResponseList(List<ConversationEntity> conversationEntities);
}