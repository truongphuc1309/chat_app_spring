package com.truongphuc.mapper;

import com.truongphuc.dto.response.MessageDetailsResponse;
import com.truongphuc.dto.response.MessageResponse;
import com.truongphuc.entity.MessageEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    MessageDetailsResponse toMessageDetailsResponse(MessageEntity messageEntity);
    List<MessageResponse> toMessageResponseList(List<MessageEntity> messageEntities);
}
