package com.truongphuc.controller;

import com.truongphuc.dto.request.MessageRequest;
import com.truongphuc.dto.response.ApiResponse;
import com.truongphuc.dto.response.MessageDetailsResponse;
import com.truongphuc.dto.response.MessageResponse;
import com.truongphuc.dto.response.PageResponse;
import com.truongphuc.service.MessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/message")
public class MessageController {
    MessageService messageService;

    @PostMapping
    public ApiResponse<MessageDetailsResponse> sendMessage(@RequestBody MessageRequest messageRequest) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        MessageDetailsResponse result = messageService.sendMessage(userEmail, messageRequest);
        return new ApiResponse<>("0000", "Success send message", result);
    }

    @GetMapping("/{id}")
    public ApiResponse<MessageDetailsResponse> getAllMessagesOfConversation(@PathVariable(name = "id") String id){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        MessageDetailsResponse result = messageService.getMessageById(userEmail, id);
        return new ApiResponse<>("0000", "Success get message info", result);
    }

    @GetMapping("/all/{id}")
    public ApiResponse<PageResponse<MessageResponse>> getAllMessagesOfConversation(
            @PathVariable(name = "id") String conversationId,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit
    ) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        PageResponse<MessageResponse> result = messageService.getAllMessagesOfConversation(userEmail, conversationId, page, limit);
        return new ApiResponse<>("0000", "Success conversation's messages", result);
    }

    @GetMapping("/last/{id}")
    public ApiResponse<MessageResponse> getLastMessageOfConversation(@PathVariable(name = "id") String conversationId) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        MessageResponse result = messageService.getLastMessageOfConversation(userEmail, conversationId);
        return new ApiResponse<>("0000", "Success conversation's last message", result);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<MessageDetailsResponse> deleteMessage(@PathVariable(name = "id") String id) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        MessageDetailsResponse result = messageService.deleteMessage(userEmail, id);
        return new ApiResponse<>("0000", "Success delete message", result);
    }
}
