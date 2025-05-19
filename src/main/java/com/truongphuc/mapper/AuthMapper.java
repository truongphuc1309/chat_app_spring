package com.truongphuc.mapper;

import com.truongphuc.dto.request.auth.SignUpRequest;
import com.truongphuc.dto.response.auth.LogInResponse;
import com.truongphuc.dto.response.auth.RefreshResponse;
import com.truongphuc.dto.response.auth.SignUpResponse;
import com.truongphuc.entity.UserEntity;

import org.mapstruct.Mapper;

@Mapper (componentModel = "spring")
public interface AuthMapper {
    UserEntity toUserEntity (SignUpRequest signUpRequest);
    SignUpResponse toSignUpResponse (UserEntity userEntity);
    LogInResponse toLogInResponse (UserEntity userEntity);
}
