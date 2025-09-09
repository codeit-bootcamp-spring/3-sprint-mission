package com.sprint.mission.discodeit.web;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtAuthenticationChannelInterceptor jwtAuthenticationChannelInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.enableSimpleBroker("/sub"); // 클라이언트 메시지 구족용

        registry.setApplicationDestinationPrefixes("/pub"); // 클라이언트 메시지 발행용
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000", "http://127.0.0.1:3000")
                .withSockJS() // SockJS 사용: WebSocket 미지원 브라우저를 위해 SockJs 프로토콜을 사용한 대체 통신 폴백
                .setSuppressCors(false); // SockJS info/폴백 응답에 CORS 헤더 노출
    }

    public AuthorizationChannelInterceptor authorizationChannelInterceptor() {
        var mgr = MessageMatcherDelegatingAuthorizationManager.builder()
                .simpSubscribeDestMatchers("/sub/**").hasAnyRole("USER", "CHANNEL_MANAGER", "ADMIN")
                .simpDestMatchers("/pub/**").hasAnyRole("USER", "CHANNEL_MANAGER", "ADMIN")
                .anyMessage().authenticated()
                .build();
        return new AuthorizationChannelInterceptor(mgr);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(
                jwtAuthenticationChannelInterceptor,
                new SecurityContextChannelInterceptor(),
                authorizationChannelInterceptor()
        );
    }
}
