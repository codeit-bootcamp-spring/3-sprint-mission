package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.ws.JwtAuthenticationChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * PackageName  : com.sprint.mission.discodeit.config
 * FileName     : WebsocketConfig
 * Author       : dounguk
 * Date         : 2025. 9. 3.
 */

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub");
        config.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .withSockJS()
            .setHeartbeatTime(1000 * 25)
            .setDisconnectDelay(1000 * 5);
    }

    @Bean
    public JwtAuthenticationChannelInterceptor jwtAuthenticationChannelInterceptor() {
        return new JwtAuthenticationChannelInterceptor(jwtTokenProvider);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(
            jwtAuthenticationChannelInterceptor(),
            new SecurityContextChannelInterceptor()
        );
    }
}
