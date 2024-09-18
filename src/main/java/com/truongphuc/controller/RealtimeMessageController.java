package com.truongphuc.controller;

import com.truongphuc.constant.MessageAction;
import com.truongphuc.dto.response.ConversationDetailsResponse;
import com.truongphuc.dto.response.MessageDetailsResponse;
import com.truongphuc.dto.response.RealtimeMessageResponse;
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
public class RealtimeMessageController {
     MessageMapper messageMapper;
     SimpMessagingTemplate simpMessagingTemplate;
     ConversationService conversationService;

     @MessageMapping ("/message")
     public void sendMessage(@Payload MessageDetailsResponse request) {
          simpMessagingTemplate.convertAndSend("/topic/conversation/" + request.getConversation().getId(),
                  new RealtimeMessageResponse(MessageAction.SEND, request));

          ConversationDetailsResponse foundConversation = conversationService.getConversationById(request.getUser().getEmail(), request.getConversation().getId());
          foundConversation.getMembers().forEach((e) -> {
               simpMessagingTemplate.convertAndSend("/topic/conversation/list/" + e.getId(), foundConversation);
          });
     }

     @MessageMapping ("/message/delete")
     public void deleteMessage(@Payload MessageDetailsResponse request) {
          simpMessagingTemplate.convertAndSend("/topic/conversation/" + request.getConversation().getId(),
                  new RealtimeMessageResponse(MessageAction.DELETE, request));
          simpMessagingTemplate.convertAndSend("/topic/message/last/" + request.getConversation().getId(), request);
     }
}
