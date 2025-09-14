package com.sprint.mission.discodeit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 및 STOMP 프로토콜 설정을 담당하는 설정 클래스message_attachments
 */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // 메시지 브로커 설정 구성
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        log.info("[WebSocketConfig] 메시지 브로커 설정 시작");

        // Spring이 지원하는 인메모리 브로커 활성화
        config.enableSimpleBroker("/sub");
        log.debug("[메시지 브로커 SimpleBroker] 구독 경로 설정: /sub (브로드캐스트)");

        config.setApplicationDestinationPrefixes("/pub");
        log.debug("[메시지 브로커 SimpleBroker] Application Destination Prefix 설정: /pub");

        log.info("[WebSocketConfig] 메시지 브로커 설정 완료");
    }

    // WebSocket 엔드포인트 등록
    // 클라이언트가 WebSocket 서버에 연결할 때 사용할 경로를 정의
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("[WebSocketConfig] STOMP 엔드포인트 등록 시작");

        registry.addEndpoint("/ws")
            // CORS 설정 - 모든 오리진에서의 접근을 허용 (개발 환경용)
            .setAllowedOriginPatterns("*")
            // SockJS 폴백 활성화
            // SockJS는 WebSocket이 불가능한 환경에서 HTTP 기반 폴링 등의 대안을 제공
            .withSockJS()
            // SockJS 설정 - 하트비트 간격과 연결 해제 지연 시간 설정 (25초마다 하트비트 전송, 5초 후 연결 해제)
            .setHeartbeatTime(25000)
            .setDisconnectDelay(5000);

        log.info("[WebSocketConfig] STOMP 엔드포인트 등록 완료");
        log.debug("[STOMP 엔드포인트] 경로: /ws, SockJS 폴백: 활성화, CORS: 모든 오리진 허용");


    }
}
