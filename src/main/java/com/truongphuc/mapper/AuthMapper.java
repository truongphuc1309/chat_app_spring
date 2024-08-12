package com.truongphuc.mapper;

import com.truongphuc.dto.request.SignUpRequest;
import com.truongphuc.dto.response.LogInResponse;
import com.truongphuc.dto.response.RefreshResponse;
import com.truongphuc.dto.response.SignUpResponse;
import com.truongphuc.entity.TokenEntity;
import com.truongphuc.entity.UserEntity;

import org.mapstruct.Mapper;

@Mapper (componentModel = "spring")
public interface AuthMapper {
    UserEntity toUserEntity (SignUpRequest signUpRequest);
    SignUpResponse toSignUpResponse (UserEntity userEntity);
    LogInResponse toLogInResponse (UserEntity userEntity);
    RefreshResponse toRefreshResponse (TokenEntity tokenEntity);
}
