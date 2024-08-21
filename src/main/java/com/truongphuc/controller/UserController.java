package com.truongphuc.controller;

import com.truongphuc.dto.request.UserUpdateRequest;
import com.truongphuc.dto.response.ApiResponse;
import com.truongphuc.dto.response.UserProfileResponse;
import com.truongphuc.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping ("/search/{key}")
    public ApiResponse<List<UserProfileResponse>> updateUser (@PathVariable("key") String key){
        List<UserProfileResponse> result = userService.searchUser(key);

        return new ApiResponse<>("0000", "Success", result);
    }
}
