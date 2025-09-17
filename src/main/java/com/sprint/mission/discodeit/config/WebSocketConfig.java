package com.sprint.mission.discodeit.config;


import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.JwtAuthenticationChannelInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.*;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtAuthenticationChannelInterceptor jwtAuthenticationChannelInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub");          // 구독 prefix
        config.setApplicationDestinationPrefixes("/pub"); // 발행 prefix
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .withSockJS();
    }

    @Bean
    public AuthorizationChannelInterceptor authorizationChannelInterceptor() {
        var authzManager = MessageMatcherDelegatingAuthorizationManager
            .builder()
            // 예: 모든 메시지는 최소 USER 권한 요구
            .anyMessage().hasRole(Role.USER.name())
            .build();
        return new AuthorizationChannelInterceptor(authzManager);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(
            jwtAuthenticationChannelInterceptor,     // 1) CONNECT 시 JWT 인증 → accessor.setUser
            new SecurityContextChannelInterceptor(), // 2) 이후 흐름에서 SecurityContext 연동
            authorizationChannelInterceptor()        // 3) 권한 검사 (hasRole(USER))
        );
    }
}
