package com.truongphuc.controller;

import com.truongphuc.dto.request.auth.ChangePasswordRequest;
import com.truongphuc.dto.request.user.UserStatusRequest;
import com.truongphuc.dto.request.user.UserUpdateRequest;
import com.truongphuc.dto.response.ApiResponse;
import com.truongphuc.dto.response.user.UserProfileResponse;
import com.truongphuc.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/user")
public class UserController {
    UserService userService;

    @GetMapping("/profile")
    public ApiResponse<UserProfileResponse> getUserProfile (){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserProfileResponse result = userService.getUserProfile(email);

        return new ApiResponse<>("0000", "Success", result);
    }

    @PatchMapping ("/update")
    public ApiResponse<UserProfileResponse> updateUser (@RequestBody UserUpdateRequest userUpdateRequest){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserProfileResponse result = userService.updateUser(email, userUpdateRequest);

        return new ApiResponse<>("0000", "Success", result);
    }

    @PostMapping ("/avatar")
    public ApiResponse<UserProfileResponse> updateAvatar (@RequestParam("avatar") MultipartFile avatar) throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserProfileResponse result = userService.updateAvatar(email, avatar);
        return new ApiResponse<>("0000", "Success", result);
    }

    @PostMapping ("/avatar/remove")
    public ApiResponse<String> removeAvatar () throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.removeAvatar(email);
        return new ApiResponse<>("0000", "Success remove avatar", "true");
    }

    @GetMapping ("/search/{key}")
    public ApiResponse<List<UserProfileResponse>> updateUser (@PathVariable("key") String key){
        List<UserProfileResponse> result = userService.searchUser(key);

        return new ApiResponse<>("0000", "Success", result);
    }

    @PostMapping ("/change-password")
    public ApiResponse<String> updateUser (@RequestBody @Valid ChangePasswordRequest changePasswordRequest){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String result = userService.changePassword(email, changePasswordRequest);
        return new ApiResponse<>("0000", "Success change password", result);
    }

    @PostMapping ("/status")
    public ApiResponse<String> updateUserStatus (@RequestBody UserStatusRequest statusRequest){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.updateUserStatus(email, statusRequest.isOnline());
        return new ApiResponse<>("0000", "Success update status", "Success");
    }
}
