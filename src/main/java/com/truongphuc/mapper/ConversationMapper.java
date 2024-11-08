package com.truongphuc.mapper;

import com.truongphuc.dto.request.conversation.ConversationCreationRequest;
import com.truongphuc.dto.response.conversation.ConversationDetailsResponse;
import com.truongphuc.dto.response.user.UserProfileResponse;
import com.truongphuc.entity.ConversationEntity;
import com.truongphuc.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper (componentModel = "spring")
public interface ConversationMapper {
    ConversationEntity toConversationEntity(ConversationCreationRequest conversationCreationRequest);

    List<ConversationDetailsResponse> toConversationResponseList(List<ConversationEntity> conversationEntities);

    @Mapping(target = "avatar", source = "avatar.url")
    UserProfileResponse toUserProfileResponse(UserEntity userEntity);

    default ConversationDetailsResponse toConversationResponse(ConversationEntity conversationEntity) {
        if ( conversationEntity == null ) {
            return null;
        }

        ConversationDetailsResponse conversationDetailsResponse = new ConversationDetailsResponse();


        if ( conversationEntity.getAvatar() != null )
            conversationDetailsResponse.setAvatar(conversationEntity.getAvatar().getUrl());
        else
            conversationDetailsResponse.setAvatar(null);
        conversationDetailsResponse.setCreatedBy( toUserProfileResponse( conversationEntity.getCreatedBy() ) );
        conversationDetailsResponse.setId( conversationEntity.getId() );
        conversationDetailsResponse.setName( conversationEntity.getName() );
        conversationDetailsResponse.setGroup( conversationEntity.isGroup() );
        conversationDetailsResponse.setCreatedAt( conversationEntity.getCreatedAt() );
        conversationDetailsResponse.setUpdatedAt( conversationEntity.getUpdatedAt() );
        conversationDetailsResponse.setMembers(new HashSet<>());

        conversationEntity.getMembers().forEach((e)-> conversationDetailsResponse.getMembers().add( toUserProfileResponse( e ) ));

        return conversationDetailsResponse;
    }
}