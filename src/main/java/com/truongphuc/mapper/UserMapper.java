package com.truongphuc.mapper;

import com.truongphuc.dto.response.user.UserProfileResponse;
import com.truongphuc.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper (componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "avatar", source = "avatar.url")
    UserProfileResponse toUserProfileResponse(UserEntity userEntity);
    List<UserProfileResponse> toUserProfileResponseList(List<UserEntity> userEntities);
}
