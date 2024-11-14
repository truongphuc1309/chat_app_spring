package com.truongphuc.filter;

import com.truongphuc.constant.ExceptionCode;
import com.truongphuc.constant.TokenType;
import com.truongphuc.exception.AppException;
import com.truongphuc.service.JwtService;
import com.truongphuc.service.UserService;
import com.truongphuc.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class WebSocketInterceptor implements ChannelInterceptor {
    final private JwtService jwtService;
    final private UserService userService;
    final private WebSocketService webSocketService;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // Authentication
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.isBlank())
                throw new AppException("Required token", ExceptionCode.INVALID_TOKEN);

            String token = authHeader.substring("Bearer ".length());

            if (token.isBlank())
                throw new AppException("Token is required", ExceptionCode.INVALID_TOKEN);

            boolean isValid = jwtService.verify(TokenType.ACCESS_TOKEN, token);

            if (!isValid)
                throw new AppException("Invalid token", ExceptionCode.INVALID_TOKEN);

            // Set authentication
            String email = jwtService.extractEmail(TokenType.ACCESS_TOKEN,token);
            UserDetails userDetails = userService.getUserDetailsService().loadUserByUsername(email);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null, userDetails.getAuthorities());
            accessor.setUser(authentication);
            webSocketService.sendUpdateUserStatus(email, true);
            log.info("Connected to websocket {}", email);

        }


        // To set user status to offline when ws disconnect
        if (accessor != null && StompCommand.DISCONNECT.equals(accessor.getCommand())){
            String email = accessor.getUser().getName();
            webSocketService.sendUpdateUserStatus(email, false);
            log.info("Disconnected to websocket {}", email);
        }

        return message;
    }
}
