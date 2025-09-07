package com.sprint.mission.discodeit.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.websocket.interceptor.JwtAuthenticationChannelInterceptor;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

class WebSocketConfigTest {

    private WebSocketConfig config;
    private ChannelInterceptor authorization;
    private final MessageChannel dummyChannel = new ExecutorSubscribableChannel();

    @BeforeEach
    void setUp() {
        JwtAuthenticationChannelInterceptor jwt = mock(JwtAuthenticationChannelInterceptor.class);
        config = new WebSocketConfig(jwt);

        authorization = (ChannelInterceptor) ReflectionTestUtils.invokeMethod(
            config, "authorizationChannelInterceptor");

        assertNotNull(authorization);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("CONNECT Type의 메시지는 허용되어야 한다.")
    void anonymous_CONNECT_permitAll() {
        // given
        Message<?> connect = messageWithType(SimpMessageType.CONNECT, null);
        // when & then
        assertDoesNotThrow(() -> authorization.preSend(connect, dummyChannel));
    }

    @Test
    @DisplayName("ROLE_USER 권한이 없는 사용자의 pub 요청은 거부 되어야 한다.")
    void anonymous_SEND_to_pub_denied() {
        // given
        SecurityContextHolder.getContext().setAuthentication(anonymousAuth());
        Message<?> sendPub = sendMessage("/pub/messages", null);
        // when & then
        assertThrows(AccessDeniedException.class,
            () -> authorization.preSend(sendPub, dummyChannel));
    }

    @Test
    @DisplayName("ROLE_USER 권한이 없는 사용자의 sub 요청은 거부 되어야 한다.")
    void anonymous_SUBSCRIBE_to_sub_denied() {
        // given
        SecurityContextHolder.getContext().setAuthentication(anonymousAuth());
        UUID channelId = UUID.randomUUID();
        Message<?> sub = subscribeMessage("/sub/channels." + channelId + ".messages", null);
        // when & then
        assertThrows(AccessDeniedException.class,
            () -> authorization.preSend(sub, dummyChannel));
    }

    @Test
    @DisplayName("ROLE_USER를 가진 사용자의 pub 요청은 허용되어야 한다.")
    void roleUser_SEND_to_pub_allowed() {
        // given
        Authentication auth = userAuth();
        SecurityContextHolder.getContext().setAuthentication(auth);
        Message<?> sendPub = sendMessage("/pub/chat.send", auth);
        // when & then
        assertDoesNotThrow(() -> authorization.preSend(sendPub, dummyChannel));
    }

    @Test
    @DisplayName("ROLE_USER를 가진 사용자의 sub 요청은 허용되어야 한다.")
    void roleUser_SUBSCRIBE_to_sub_allowed() {
        // given
        Authentication auth = userAuth();
        SecurityContextHolder.getContext().setAuthentication(auth);
        UUID channelId = UUID.randomUUID();
        Message<?> sub = subscribeMessage("/sub/channels." + channelId + ".messages", auth);
        // when & then
        assertDoesNotThrow(() -> authorization.preSend(sub, dummyChannel));
    }


    private Authentication anonymousAuth() {
        return new AnonymousAuthenticationToken(
            "key", "anonymousUser",
            List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
        );
    }

    private Authentication userAuth() {
        return new AnonymousAuthenticationToken(
            "key", "simpleUser",
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    private Message<?> sendMessage(String destination, Object principal) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(
            SimpMessageType.MESSAGE);
        accessor.setDestination(destination);
        if (principal != null) {
            accessor.setUser(() -> principal.toString());
        }
        accessor.setLeaveMutable(true);
        return MessageBuilder.withPayload("hi").setHeaders(accessor).build();
    }

    private Message<?> subscribeMessage(String destination, Object principal) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(
            SimpMessageType.SUBSCRIBE);
        accessor.setDestination(destination);
        if (principal != null) {
            accessor.setUser(() -> principal.toString());
        }
        accessor.setLeaveMutable(true);
        return MessageBuilder.withPayload(new byte[0]).setHeaders(accessor).build();
    }

    private Message<?> messageWithType(SimpMessageType type, Object principal) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(type);

        if (principal != null) {
            accessor.setUser(() -> principal.toString());
        }
        accessor.setLeaveMutable(true);
        return MessageBuilder.withPayload(new byte[0]).setHeaders(accessor).build();
    }
}