package com.sprint.mission.discodeit.security.jwt;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            log.debug("[WS] CONNECT Authorization header={}", authHeader);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("[WS] Authorization header missing or invalid");
                throw new AccessDeniedException("Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7);

            // JWT 검증
            if (!jwtTokenProvider.validateAccessToken(token)) {
                log.warn("[WS] JWT validation failed: {}", token);
                throw new AccessDeniedException("Invalid JWT token");
            }

            // 사용자 정보 추출
            String username = jwtTokenProvider.getUsernameFromToken(token);
            Collection<? extends GrantedAuthority> authorities =
                    jwtTokenProvider.getAuthorities(token);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            // SecurityContext 대신 accessor 에 저장
            accessor.setUser(authentication);

            log.debug("[WS] CONNECT 인증 성공: username={}, authorities={}", username, authorities);
        }

        return message;
    }
}