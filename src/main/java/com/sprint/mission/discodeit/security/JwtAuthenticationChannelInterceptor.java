package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * STOMP CONNECT 시 Authorization 헤더의 JWT를 검증하여 accessor.setUser(authentication) 에 저장.
 * 검증 로직은 기존 JwtAuthenticationFilter / JwtTokenProvider 와 동일 규칙을 사용한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

    private final DiscodeitUserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String bearer = firstNativeHeader(accessor, "Authorization")
                .or(() -> firstNativeHeader(accessor, "authorization"))
                .orElseThrow(() -> new BadCredentialsException("Missing Authorization header"));

            if (!bearer.startsWith("Bearer ")) {
                throw new BadCredentialsException("Invalid Authorization header");
            }
            String token = bearer.substring("Bearer ".length()).trim();

            // private validateToken(..) 말고 공개 메서드 사용
            if (!jwtTokenProvider.validateAccessToken(token)) {
                throw new BadCredentialsException("Invalid or expired ACCESS token");
            }

            // getUsernameFromToken(..) 사용 (getUsername 아님)
            String username = jwtTokenProvider.getUsernameFromToken(token);

            var userDetails = userDetailsService.loadUserByUsername(username);
            var authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
            );

            // SecurityContext 대신 accessor에 저장 → 이후 SecurityContextChannelInterceptor가 이어받음
            accessor.setUser(authentication);
            log.debug("STOMP CONNECT authenticated: {}", username);
        }
        return message;
    }

    private Optional<String> firstNativeHeader(StompHeaderAccessor accessor, String name) {
        List<String> values = accessor.getNativeHeader(name);
        return (values == null || values.isEmpty()) ? Optional.empty() : Optional.ofNullable(values.get(0));
    }
}
