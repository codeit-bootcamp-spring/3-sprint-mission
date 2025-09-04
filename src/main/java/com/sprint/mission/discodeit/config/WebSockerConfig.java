package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.websocket.interceptor.WebSocketInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.TaskExecutorRegistration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSockerConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketInterceptor webSocketInterceptor;

    @Value("${websocket.executors.core-size}")
    private int MAX_CORE_SIZE;

    @Value("${websocket.executors.max-size}")
    private int MAX_POOL_SIZE;

    @Value("${websocket.executors.queue-capacity}")
    private int MAX_QUEUE_CAPACITY;

    /**
     * 메시지 브로커 설정 (메모리 기반) STOMP 프로토콜에서 사용할 메시지 라우팅 규칙 정의
     *
     * @param config 메시지 브로커 설정 객체
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        log.debug("[WebSocketConfig] 메시지 브로커 설정 시작");

        config.enableSimpleBroker("/sub");

        config.setApplicationDestinationPrefixes("/pub");

        log.debug("[WebSocketConfig] 메시지 브로커 설정 완료");
    }

    /**
     * WebSocket 엔드포인트를 등록한다 클라이언트가 WebSocket 서버에 연결할 때 사용할 경로 정의
     *
     * @param registry STOMP 엔드포인트 등록 객체
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        log.debug("[WebSocketConfig] STOMP 엔드포인트 등록 시작");

        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .withSockJS()
            .setHeartbeatTime(25000)
            .setDisconnectDelay(5000);

        log.debug("[WebSocketConfig] STOMP 엔드포인트 등록 완료");
    }

    /**
     * 클라이언트 인바운드 채널 설정 클라이언트에서 서버로 들어오는 메시지 처리를 위한 스레드 풀을 설정하고 전용 인터셉터 등록
     *
     * @param registration 채널 등록 객체
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {

        log.debug("[WebSocketConfig] 클라이언트 인바운드 채널 설정 시작");

        setTaskExecutorRegistration(registration);

        registration.interceptors(webSocketInterceptor);

        log.debug("[WebSocketConfig] 클라이언트 인바운드 채널 설정 완료");
    }

    /**
     * 클라이언트 아웃바운드 채널 설정 서버에서 클라이언트로 들어오는 메시지 처리를 위한 스레드 풀을 설정
     *
     * @param registration 채널 등록 객체
     */
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {

        log.debug("[WebSocketConfig] 클라이언트 아웃바운드 채널 설정 시작");

        setTaskExecutorRegistration(registration);

        registration.interceptors(webSocketInterceptor);

        log.debug("[WebSocketConfig] 클라이언트 아웃바운드 채널 설정 완료");
    }

    private TaskExecutorRegistration setTaskExecutorRegistration(ChannelRegistration registration) {
        return registration.taskExecutor()
            .corePoolSize(MAX_CORE_SIZE)
            .maxPoolSize(MAX_POOL_SIZE)
            .queueCapacity(MAX_QUEUE_CAPACITY);
    }
}
