package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

/**
 * PackageName  : com.sprint.mission.discodeit.config
 * FileName     : MessagingAuthorizationConfig
 * Author       : dounguk
 * Date         : 2025. 9. 9.
 */

@Configuration
public class MessagingAuthorizationConfig {

    @Bean
    public AuthorizationChannelInterceptor authorizationChannelInterceptor() {
        var manager = MessageMatcherDelegatingAuthorizationManager.builder()
            .simpTypeMatchers(SimpMessageType.HEARTBEAT, SimpMessageType.UNSUBSCRIBE, SimpMessageType.DISCONNECT).permitAll()
            .simpDestMatchers("/pub/**").hasRole(Role.USER.name())
            .simpSubscribeDestMatchers("/sub/**").hasRole(Role.USER.name())
            .anyMessage().hasRole(Role.USER.name())
            .build();

        return new AuthorizationChannelInterceptor(manager);
    }
}