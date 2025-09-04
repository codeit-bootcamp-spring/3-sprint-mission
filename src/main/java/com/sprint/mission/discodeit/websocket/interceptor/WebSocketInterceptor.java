package com.sprint.mission.discodeit.websocket.interceptor;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

/**
 * WebSocket 채널 인터셉터 클래스 STOMP 메시지 송수신 과정에서 로깅 및 모니터링 기능 처리
 * <p>
 * 주요 기능: - WebSocket 연결/해제 이벤트 로깅 - 메시지 송수신 로깅 및 모니터링 - 기본적인 세션 관리
 */
@Slf4j
@Component
public class WebSocketInterceptor implements ChannelInterceptor {

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
}
