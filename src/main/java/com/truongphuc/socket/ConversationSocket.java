package com.truongphuc.socket;

import com.truongphuc.constant.ConversationAction;
import com.truongphuc.dto.response.conversation.ConversationDetailsResponse;
import com.truongphuc.dto.response.conversation.ConversationSocketMessage;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
public class ConversationSocket {
    SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/conversation")
    public void sendMessage(@Payload ConversationDetailsResponse request) {
        ConversationSocketMessage message = ConversationSocketMessage.builder()
                .action(ConversationAction.ADD)
                .data(request)
                .build();
        request.getMembers().forEach((e) -> {
            simpMessagingTemplate.convertAndSend("/topic/conversation/list/" + e.getId(), message);
        });
    }

    @MessageMapping("/conversation/change-data")
    public void changeData(@Payload ConversationDetailsResponse request) {
        simpMessagingTemplate.convertAndSend("/topic/conversation/" + request.getId(), request);
    }

    @MessageMapping("/conversation/delete")
    public void deleteConversation(@Payload ConversationDetailsResponse request) {
        ConversationDetailsResponse data = ConversationDetailsResponse.builder()
               .id(request.getId())
               .build();

        ConversationSocketMessage message = ConversationSocketMessage.builder()
                .action(ConversationAction.DELETE)
                .data(data)
                .build();

        request.getMembers().forEach((e) -> {
            simpMessagingTemplate.convertAndSend("/topic/conversation/list/" + e.getId(), message);
        });
    }
}
