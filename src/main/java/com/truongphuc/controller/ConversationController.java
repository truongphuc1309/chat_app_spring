package com.truongphuc.controller;

import com.truongphuc.dto.request.conversation.*;
import com.truongphuc.dto.response.*;
import com.truongphuc.dto.response.conversation.ConversationAvatarChangeResponse;
import com.truongphuc.dto.response.conversation.ConversationDetailsResponse;
import com.truongphuc.dto.response.conversation.RenameConversationResponse;
import com.truongphuc.service.ConversationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/conversation")
public class ConversationController {
    ConversationService conversationService;

    @PostMapping
    public ApiResponse<ConversationDetailsResponse> createConversation(@RequestBody @Valid ConversationCreationRequest request){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ConversationDetailsResponse response = conversationService.createConversation(userEmail, request);
        return new ApiResponse<>("0000","Success created conversation", response);
    }

    @GetMapping("/{id}")
    public ApiResponse<ConversationDetailsResponse> getConversationDetails(@PathVariable(name = "id") String conversationId){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ConversationDetailsResponse response = conversationService.getConversationById(userEmail, conversationId);
        return new ApiResponse<>("0000","Success get conversation details", response);
    }

    @GetMapping("/user/{id}")
    public ApiResponse<ConversationDetailsResponse> getSingleConversationByUser(@PathVariable(name = "id") String userId){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ConversationDetailsResponse response = conversationService.getSingleConversationByUser(userEmail, userId);
        return new ApiResponse<>("0000","Success get single conversation", response);
    }

    @GetMapping("/all")
    public ApiResponse<PageResponse<ConversationDetailsResponse>> getAllConversationsOfUser(@RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                                                            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        PageResponse<ConversationDetailsResponse> response = conversationService.getAllConversationsOfUser(userEmail, page, limit);
        return new ApiResponse<>("0000","Success get conversations", response);
    }

    @PostMapping("/add")
    public ApiResponse<ConversationDetailsResponse> addMemberToConversation(@RequestBody AddMemberToConversationRequest request){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ConversationDetailsResponse response = conversationService.addMemberToConversation(userEmail, request);
        return new ApiResponse<>("0000","Success add new member", response);
    }

    @PatchMapping("/rename")
    public ApiResponse<RenameConversationResponse> renameConversation (@RequestBody RenameConversationRequest request){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        RenameConversationResponse response = conversationService.renameConversation(userEmail, request);
        return new ApiResponse<>("0000","Success rename conversation", response);
    }

    @PostMapping("/avatar")
    public ApiResponse<ConversationAvatarChangeResponse> changeAvatarConversation (@RequestParam("conversationId") String conversationId, @RequestParam("avatar") MultipartFile avatar) throws Exception {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ConversationAvatarChangeRequest conversationAvatarChangeRequest = new ConversationAvatarChangeRequest(conversationId, avatar);
        ConversationAvatarChangeResponse response = conversationService.changeAvatarConversation(userEmail, conversationAvatarChangeRequest);
        return new ApiResponse<>("0000","Success change conversation's conversation", response);
    }

    @PostMapping("/avatar/remove/{id}")
    public ApiResponse<String> removeAvatarConversation (@PathVariable("id") String conversationId) throws Exception {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        conversationService.removeAvatarConversation(userEmail, conversationId);
        return new ApiResponse<>("0000","Success remove conversation's conversation", "true");
    }


    @PostMapping ("/remove")
    public ApiResponse<Boolean> removeFromConversation (@RequestBody RemoveFromConversationRequest request){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean response = conversationService.removeFromConversation(userEmail, request);
        return new ApiResponse<>("0000","Success remove member", response);
    }

    @DeleteMapping ("/{id}")
    public ApiResponse<Boolean> deleteConversation (@PathVariable(name = "id") String conversationId){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean response = conversationService.deleteConversation(userEmail, conversationId);
        return new ApiResponse<>("0000","Success delete conversation", response);
    }
}
