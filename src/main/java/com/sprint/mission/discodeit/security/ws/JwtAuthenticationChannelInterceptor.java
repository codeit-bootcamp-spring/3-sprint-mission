package com.sprint.mission.discodeit.security.ws;

import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * PackageName  : com.sprint.mission.discodeit.security.ws
 * FileName     : JwtAuthenticationChannelInterceptor
 * Author       : dounguk
 * Date         : 2025. 9. 8.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 1) Authorization 헤더에서 Bearer 토큰 추출
            String rawAuth = firstHeaderIgnoreCase(accessor, "Authorization");
            String token = resolveBearer(rawAuth);
            if (!StringUtils.hasText(token)) {
                throw new MessagingException("Missing Authorization: Bearer <token>");
            }

            // 2) JWT 유효성 검사 (서명/만료 + type=access)
            if (!jwtTokenProvider.validateAccessToken(token)) {
                throw new MessagingException("Invalid or expired access token");
            }

            // 3) 클레임에서 사용자, 권한 파싱
            String username = jwtTokenProvider.getUsernameFromToken(token);
            // roles는 토큰에 저장돼 있으므로 직접 파싱
            Collection<SimpleGrantedAuthority> authorities = extractAuthorities(token);

            // 4) SecurityContext 대신 accessor에 Principal 저장
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

            accessor.setUser(authentication);
            log.debug("STOMP CONNECT authenticated: username={}, roles={}", username,
                authorities.stream().map(SimpleGrantedAuthority::getAuthority).toList());
        }

        return message;
    }

    private static String resolveBearer(String header) {
        if (!StringUtils.hasText(header)) return null;
        String h = header.trim();
        if (h.toLowerCase(Locale.ROOT).startsWith("bearer ")) {
            return h.substring(7).trim();
        }
        return null;
    }

    private static String firstHeaderIgnoreCase(StompHeaderAccessor accessor, String name) {
        String v = accessor.getFirstNativeHeader(name);
        if (!StringUtils.hasText(v)) v = accessor.getFirstNativeHeader(name.toLowerCase(Locale.ROOT));
        if (!StringUtils.hasText(v)) v = accessor.getFirstNativeHeader(name.toUpperCase(Locale.ROOT));
        return v;
    }

    private static Collection<SimpleGrantedAuthority> extractAuthorities(String token) {
        try {
            var claims = SignedJWT.parse(token).getJWTClaimsSet();
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.getClaim("roles");
            if (roles == null) roles = List.of();
            return roles.stream()
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        } catch (ParseException e) {
            throw new MessagingException("Failed to parse roles from token", e);
        }
    }
}
