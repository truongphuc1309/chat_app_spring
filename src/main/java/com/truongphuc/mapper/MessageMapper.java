package com.truongphuc.mapper;

import com.truongphuc.dto.response.message.MessageDetailsResponse;
import com.truongphuc.dto.response.message.MessageResponse;
import com.truongphuc.entity.MessageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(target = "user.avatar", source = "user.avatar.url")
    MessageDetailsResponse toMessageDetailsResponse(MessageEntity messageEntity);

    @Mapping(target = "user.avatar", source = "user.avatar.url")
    MessageResponse toMessageResponse (MessageEntity messageEntity);
    List<MessageResponse> toMessageResponseList(List<MessageEntity> messageEntities);
}
