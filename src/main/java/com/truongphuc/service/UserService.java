package com.truongphuc.service;

import com.truongphuc.dto.request.auth.ChangePasswordRequest;
import com.truongphuc.dto.request.user.UserUpdateRequest;
import com.truongphuc.dto.response.user.UserProfileResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    UserDetailsService getUserDetailsService ();

    UserProfileResponse getUserProfile (String email);

    UserProfileResponse updateUser (String email, UserUpdateRequest userUpdateRequest);

    UserProfileResponse updateAvatar(String email, MultipartFile avatar) throws Exception;

    void removeAvatar(String email) throws Exception;

    List<UserProfileResponse> searchUser (String key);

    String changePassword(String userEmail,ChangePasswordRequest changePasswordRequest);

    UserProfileResponse updateUserStatus(String email, boolean online);
}
