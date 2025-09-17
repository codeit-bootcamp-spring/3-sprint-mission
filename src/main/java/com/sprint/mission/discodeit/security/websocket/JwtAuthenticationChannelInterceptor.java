package com.sprint.mission.discodeit.security.websocket;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.COMMIT.equals(accessor.getCommand())) {
            log.debug("WebSocket CONNECT 프레임 인증 시작");

            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("WebSocket 연결 시도 - Authorization 헤더 없음 또는 형식 오류");
                throw new IllegalArgumentException("Authorization 헤더가 필요합니다!");
            }

            try {
                String token = authHeader.substring(7);

                // JWT 토큰 검증
                if (!jwtTokenProvider.validateToken(token)) {
                    log.warn("WebSocket 연결 시도 - 유효하지 않은 JWT 토큰");
                    throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
                }

                // 토큰 활성 상태인지 확인
                if (!jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
                    log.warn("WebSocket 연결 시도 - 비활성 JWT 토큰");
                    throw new IllegalArgumentException("비활성화된 토큰입니다.");
                }

                // 토큰 타입 확인
                String tokenType = jwtTokenProvider.getTokenType(token);
                if (!"access".equals(tokenType)) {
                    log.warn("WebSocket 연결 시도 - 잘못된 토큰 타입 : {}", tokenType);
                    throw new IllegalArgumentException("액세스 토큰만 사용할 수 있습니다.");
                }

                // 토큰에서 사용자 정보 추출
                DiscodeitUserDetails userDetails = createUserDetailsFromToken(token);

                // 인증 객체 생성
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );

                // StompHeaderAccessor에 사용자 정보 설정
                accessor.setUser(authentication);

                log.info("WebSocket 인증 성공 - 사용자 : {}, 역할 : {}",
                    userDetails.getUsername(),
                    userDetails.getUserDto().role());

            } catch (Exception e) {
                log.error("WebSocket 인증 실패 : {}", e.getMessage());
                throw new IllegalArgumentException("인증에 실패했습니다." + e.getMessage());
            }
        }

        return message;
    }

    /**
     * JWT 토큰에서 DiscodeitUserDetials 객체를 생성
     * */
    private DiscodeitUserDetails createUserDetailsFromToken(String token) {
        try {
            String username = jwtTokenProvider.extractUsername(token);
            UUID userId = jwtTokenProvider.extractUserId(token);
            String email = jwtTokenProvider.extractEmail(token);
            List<String> roles = jwtTokenProvider.extractRoles(token);

            // Role 추출 ( Role_ 프리픽스 제거 )
            Role role = roles.stream()
                .map(roleStr -> roleStr.startsWith("Role_") ? roleStr.substring(5) : roleStr)
                .map(Role::valueOf)
                .findFirst()
                .orElse(Role.USER);

            // UserDto 생성 ( WebSocket에서는 최소한의 정보만 필요 )
            UserDto userDto = new UserDto(
                userId,
                username,
                email,
                role,
                null, // 웹소켓에서는 불필요
                null  // 웹소켓에서는 불필요
            );

            return new DiscodeitUserDetails(userDto, null, null);

        } catch (Exception e) {
            log.error("토큰에서 사용자 정보 추출 실패 : {}", e.getMessage());
            throw new IllegalArgumentException("유효하지 않은 토큰 정보입니다.");
        }
    }
}