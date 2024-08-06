package com.truongphuc.mapper;

import com.truongphuc.dto.request.SignUpRequest;
import com.truongphuc.dto.response.LogInResponse;
import com.truongphuc.dto.response.SignUpResponse;
import com.truongphuc.entity.UserEntity;

import org.mapstruct.Mapper;

@Mapper (componentModel = "spring")
public interface AuthMapper {
    public UserEntity toUserEntity (SignUpRequest signUpRequest);
    public SignUpResponse toSignUpResponse (UserEntity userEntity);
    public LogInResponse toLogInResponse (UserEntity userEntity);
}
