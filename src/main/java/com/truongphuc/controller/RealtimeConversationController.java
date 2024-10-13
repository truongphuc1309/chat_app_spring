package com.truongphuc.controller;

import com.truongphuc.dto.response.conversation.ConversationDetailsResponse;
import com.truongphuc.mapper.MessageMapper;
import com.truongphuc.service.ConversationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
public class RealtimeConversationController {
    MessageMapper messageMapper;
    SimpMessagingTemplate simpMessagingTemplate;
    ConversationService conversationService;

    @MessageMapping("/conversation")
    public void sendMessage(@Payload ConversationDetailsResponse request) {
        request.getMembers().forEach((e) -> {
            simpMessagingTemplate.convertAndSend("/topic/conversation/list/" + e.getId(), request);
        });
    }
}
