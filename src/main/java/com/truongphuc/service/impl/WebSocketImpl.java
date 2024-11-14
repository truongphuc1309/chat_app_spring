package com.truongphuc.service.impl;

import com.truongphuc.dto.response.user.UserProfileResponse;
import com.truongphuc.service.UserService;
import com.truongphuc.service.WebSocketService;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketImpl implements WebSocketService {
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketImpl(@Lazy SimpMessagingTemplate simpMessagingTemplate, UserService userService) {
        this.messagingTemplate = simpMessagingTemplate;
        this.userService = userService;
    }

    @Override
    public void sendUpdateUserStatus(String email, boolean online) {
        UserProfileResponse response = userService.updateUserStatus(email, online);
        messagingTemplate.convertAndSend("/topic/user/" + response.getId(), response);
    }
}
