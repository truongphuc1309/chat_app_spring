package com.truongphuc.controller;

import com.truongphuc.dto.request.message.MessageRequest;
import com.truongphuc.dto.request.message.ReadLastMessageRequest;
import com.truongphuc.dto.response.ApiResponse;
import com.truongphuc.dto.response.message.LastReadMessageResponse;
import com.truongphuc.dto.response.message.MessageDetailsResponse;
import com.truongphuc.dto.response.message.MessageResponse;
import com.truongphuc.dto.response.PageResponse;
import com.truongphuc.service.MessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RequiredArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/message")
public class MessageController {
    MessageService messageService;

    @PostMapping
    public ApiResponse<MessageDetailsResponse> sendMessage(
            @RequestParam("conversationId") String conversationId,
            @RequestParam(value = "content", defaultValue = "") byte[] content,
            @RequestParam("type") String type,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        byte[] decodedBytes = Base64.getDecoder().decode(content);
        String message = new String(decodedBytes, StandardCharsets.UTF_8);

        MessageRequest messageRequest = MessageRequest.builder()
                .conversationId(conversationId)
                .content(message)
                .file(file)
                .type(type)
                .build();
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
    public ApiResponse<MessageDetailsResponse> deleteMessage(@PathVariable(name = "id") String id) throws Exception {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        MessageDetailsResponse result = messageService.deleteMessage(userEmail, id);
        return new ApiResponse<>("0000", "Success delete message", result);
    }

    @PostMapping("/last-read/{id}")
    public ApiResponse<LastReadMessageResponse> readLastMessage(@PathVariable(name = "id") String id) throws Exception {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        LastReadMessageResponse result = messageService.readLastMessageOfConversation(userEmail, id);
        return new ApiResponse<>("0000", "Success ", result);
    }
}
