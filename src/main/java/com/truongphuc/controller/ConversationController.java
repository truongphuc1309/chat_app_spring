package com.truongphuc.controller;

import com.truongphuc.dto.request.AddMemberToConversationRequest;
import com.truongphuc.dto.request.ConversationCreationRequest;
import com.truongphuc.dto.request.RemoveFromConversationRequest;
import com.truongphuc.dto.request.RenameConversationRequest;
import com.truongphuc.dto.response.ApiResponse;
import com.truongphuc.dto.response.ConversationDetailsResponse;
import com.truongphuc.dto.response.PageResponse;
import com.truongphuc.dto.response.RenameConversationResponse;
import com.truongphuc.service.ConversationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/conversation")
public class ConversationController {
    ConversationService conversationService;

    @PostMapping
    public ApiResponse<ConversationDetailsResponse> createConversation(@RequestBody ConversationCreationRequest request){
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
