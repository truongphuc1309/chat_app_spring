package com.truongphuc.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import com.truongphuc.dto.request.conversation.AddMemberToConversationRequest;
import com.truongphuc.dto.request.conversation.ConversationAvatarChangeRequest;
import com.truongphuc.dto.request.conversation.ConversationCreationRequest;
import com.truongphuc.dto.request.conversation.RemoveFromConversationRequest;
import com.truongphuc.dto.request.conversation.RenameConversationRequest;
import com.truongphuc.dto.response.ApiResponse;
import com.truongphuc.dto.response.PageResponse;
import com.truongphuc.dto.response.conversation.ConversationAvatarChangeResponse;
import com.truongphuc.dto.response.conversation.ConversationDetailsResponse;
import com.truongphuc.dto.response.conversation.RenameConversationResponse;
import com.truongphuc.service.ConversationService;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

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
    public ApiResponse<PageResponse<ConversationDetailsResponse>> getAllConversationsOfUser(@Min(1) @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                                                           @Min(1) @RequestParam(name = "limit", required = false, defaultValue = "10") int limit){
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
