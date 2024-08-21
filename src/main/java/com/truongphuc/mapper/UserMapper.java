package com.truongphuc.mapper;

import com.truongphuc.dto.response.UserProfileResponse;
import com.truongphuc.entity.UserEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper (componentModel = "spring")
public interface UserMapper {
    UserProfileResponse toUserProfileResponse(UserEntity userEntity);
    List<UserProfileResponse> toUserProfileResponseList(List<UserEntity> userEntities);
}
