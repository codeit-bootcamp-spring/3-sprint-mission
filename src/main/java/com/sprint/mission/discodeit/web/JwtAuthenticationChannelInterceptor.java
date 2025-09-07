package com.sprint.mission.discodeit.web;

import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.store.JwtRegistry;
import com.sprint.mission.discodeit.service.DiscodeitUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

    private static final String INTERCEPTOR_NAME = "[JwtAuthenticationChannelInterceptor] ";

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final DiscodeitUserDetailsService discodeitUserDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String auth = firstNonNull(
                    accessor.getFirstNativeHeader("Authorization"),
                    accessor.getFirstNativeHeader("authorization")
            );
            if (auth == null) throw new MessagingException(INTERCEPTOR_NAME + "Authorization 헤더 없음");

            auth = auth.trim();
            if (auth.length() < 7 || !auth.regionMatches(true, 0, "Bearer ", 0, 7)) {
                throw new MessagingException(INTERCEPTOR_NAME + "형식 오류: Authorization: Bearer <token>");
            }
            String token = auth.substring(7).trim();

            if (!jwtTokenProvider.validateAccessToken(token)
                    || !jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
                throw new MessagingException(INTERCEPTOR_NAME + "유효하지 않거나 비활성인 JWT 토큰");
            }

            String username = jwtTokenProvider.getUsernameFromToken(token);
            UserDetails user = discodeitUserDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities());

            accessor.setUser(authenticationToken);
        }
        return message;
    }

    private static String firstNonNull(String first, String second) { return (first != null) ? first : second; }
}
