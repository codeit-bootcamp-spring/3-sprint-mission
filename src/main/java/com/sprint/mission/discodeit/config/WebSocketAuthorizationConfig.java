package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
public class WebSocketAuthorizationConfig {

    @Bean
    public AuthorizationChannelInterceptor authorizationChannelInterceptor() {
        return new AuthorizationChannelInterceptor(
            MessageMatcherDelegatingAuthorizationManager.builder()
                .anyMessage().hasRole("USER")
                .build()
        );
    }

}
