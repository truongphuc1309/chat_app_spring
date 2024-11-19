package com.truongphuc.mapper;

import com.truongphuc.dto.MemberDto;
import com.truongphuc.dto.response.user.UserProfileResponse;
import com.truongphuc.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper (componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "avatar", source = "avatar.url")
    UserProfileResponse toUserProfileResponse(UserEntity userEntity);

    @Mapping(target = "avatar", source = "avatar.url")
    MemberDto toMemberDto(UserEntity userEntity);

    @Mapping(target = "avatar", source = "avatar.url")
    List<UserProfileResponse> toUserProfileResponseList(List<UserEntity> userEntities);

    @Mapping(target = "avatar", source = "avatar.url")
    Set<UserProfileResponse> toUserProfileResponseSet(Set<UserEntity> userEntities);
}
