package com.truongphuc.service;

import com.truongphuc.dto.request.UserUpdateRequest;
import com.truongphuc.dto.response.UserProfileResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService {
    UserDetailsService getUserDetailsService ();
    UserProfileResponse getUserProfile (String email);
    UserProfileResponse updateUser (String email, UserUpdateRequest userUpdateRequest);
    List<UserProfileResponse> searchUser (String key);
}
