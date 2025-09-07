package com.sprint.mission.discodeit.websocket.interceptor;

import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * WebSocket 채널 인터셉터 클래스 STOMP 메시지 송수신 과정에서 로깅 및 모니터링 기능, 액세스 토큰 검증 처리
 * <p>
 * 주요 기능: - WebSocket 연결/해제 이벤트 로깅 - 메시지 송수신 로깅 및 모니터링 - 기본적인 세션 관리 - CONNECT 프레임일 때 액세스 토큰 검증
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final DiscodeitUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
            StompHeaderAccessor.class);

        if (accessor == null) {
            log.warn("[WebSocketInterceptor] STOMP 헤더 접근자를 생성할 수 없음");
            return message;
        }

        StompCommand command = accessor.getCommand();
        if (command == null) {
            return message;
        }
        log.debug("[WebSocketInterceptor] STOMP 명령 처리 시작: {}", command);

        switch (command) {
            case CONNECT:
                handleConnect(accessor);
                break;
            case SUBSCRIBE:
                handleSubscribe(accessor);
                break;
            case SEND:
                handleSend(accessor);
                break;
            case DISCONNECT:
                handleDisconnect(accessor);
                break;
            default:
                log.debug("[WebSocketInterceptor] 처리되지 않은 STOMP 명령: {}", command);
        }

        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        if (!sent) {
            log.warn("[WebSocketInterceptor] 메시지 전송 실패: {}", message.getHeaders());
        } else {
            log.debug("[WebSocketInterceptor] 메시지 전송 성공");
        }
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent,
        Exception ex) {
        if (ex != null) {
            log.error("[WebSocketInterceptor] 메시지 전송 실패: {}", ex.getMessage());
        }
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        log.info("[WebSocket 연결] 새로운 WebSocket 연결 시도");

        String authHeader = accessor.getFirstNativeHeader("Authorization");
        String token = resolveToken(authHeader);

        if (jwtTokenProvider.validateAccessToken(token)) {

            log.info("[WebSocketInterceptor] 엑세스 토큰 검증 성공");

            // subject에 저장된 사용자명 추출
            String username = jwtTokenProvider.getUsernameFromToken(token);
            // 사용자 정보 가져오기
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );

            accessor.setUser(authentication);
        } else {
            throw new IllegalArgumentException("Invalid JWT");
        }

        // 기본적인 세션 정보 저장
        accessor.getSessionAttributes().put("connectTime", System.currentTimeMillis());

        log.info("[WebSocketInterceptor] WebSocket 연결 성공");
    }

    private void handleSubscribe(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        log.info("[WebSocketInterceptor] 구독 요청 -  destination: {}", destination);
    }

    private void handleSend(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        log.debug("[WebSocketInterceptor] 메시지 전송 -  destination: {}", destination);
    }

    private void handleDisconnect(StompHeaderAccessor accessor) {
        // 연결 시간 계산
        Long connectTime = (Long) Objects.requireNonNull(accessor.getSessionAttributes())
            .get("connectTime");
        long sessionDuration = connectTime != null ?
            System.currentTimeMillis() - connectTime : 0;

        log.info("[WebSocketInterceptor] 연결 해제 - 세션 지속시간: {}ms", sessionDuration);

        // 세션 정리
        accessor.getSessionAttributes().clear();
    }

    private String resolveToken(String authHeader) {
        if (authHeader == null) {
            return null;
        }
        if (authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }
}
