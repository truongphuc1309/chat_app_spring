package com.truongphuc.mapper;

import com.truongphuc.dto.ConversationDto;
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
    ConversationDto toConversationDto(ConversationEntity conversationEntity);

    @Mapping(target = "avatar", source = "avatar.url")
    @Mapping(target = "createdBy.avatar", source = "createdBy.avatar.url")
    ConversationDetailsResponse toConversationResponse(ConversationEntity conversationEntity) ;

    @Mapping(target = "avatar", source = "avatar.url")
    @Mapping(target = "createdBy.avatar", source = "createdBy.avatar.url")
    List<ConversationDto> toConversationDtoList(List<ConversationEntity> conversationEntities);


    List<ConversationDetailsResponse> toConversationResponseList(List<ConversationDto> conversationDtoList);

    @Mapping(target = "avatar", source = "avatar.url")
    UserProfileResponse toUserProfileResponse(UserEntity userEntity);

    default ConversationDetailsResponse toConversationResponse(ConversationDto conversationDto) {
        if ( conversationDto == null ) {
            return null;
        }

        ConversationDetailsResponse conversationDetailsResponse = new ConversationDetailsResponse();

        if ( conversationDto.getAvatar() != null )
            conversationDetailsResponse.setAvatar(conversationDto.getAvatar().getUrl());
        else
            conversationDetailsResponse.setAvatar(null);
        conversationDetailsResponse.setCreatedBy( toUserProfileResponse( conversationDto.getCreatedBy() ) );
        conversationDetailsResponse.setId( conversationDto.getId() );
        conversationDetailsResponse.setName( conversationDto.getName() );
        conversationDetailsResponse.setGroup( conversationDto.isGroup() );
        conversationDetailsResponse.setCreatedAt( conversationDto.getCreatedAt() );
        conversationDetailsResponse.setUpdatedAt( conversationDto.getUpdatedAt() );
        conversationDetailsResponse.setMembers(new HashSet<>());

        conversationDetailsResponse.setMembers(conversationDto.getMembers());

        return conversationDetailsResponse;
    }
}