package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final DiscodeitUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null) {
                throw new IllegalArgumentException("Missing Authorization header in WebSocket CONNECT");
            }

            String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authHeader;

            if (!jwtTokenProvider.validateAccessToken(token)) {
                throw new IllegalArgumentException("Invalid JWT token in WebSocket CONNECT");
            }

            UUID userId = jwtTokenProvider.getUserIdFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserById(userId);

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            accessor.setUser(authentication);
        }

        return message;
    }
}