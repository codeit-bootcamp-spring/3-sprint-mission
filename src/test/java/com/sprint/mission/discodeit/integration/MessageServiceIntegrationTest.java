package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@Transactional
@Import(MessageServiceIntegrationTest.AllowAllMethodSecurity.class)
@DisplayName("MessageService 통합 테스트")
public class MessageServiceIntegrationTest {

    @Autowired private UserService userService;
    @Autowired private ChannelService channelService;
    @Autowired private MessageService messageService;
    @Autowired private MessageRepository messageRepository;

    @Autowired private EntityManager em;

    @TestConfiguration
    static class AllowAllMethodSecurity {
        @Bean("preAuthorizeAuthorizationMethodInterceptor")
        @Primary
        AuthorizationManagerBeforeMethodInterceptor preAuthorizeAllowAll() {
            return AuthorizationManagerBeforeMethodInterceptor.preAuthorize(
                (auth, mi) -> new AuthorizationDecision(true)
            );
        }
    }
    private void authenticateAs(UserDto user, Role role) {
        var principal = new DiscodeitUserDetails(user, "N/A", role);
        var auth = new UsernamePasswordAuthenticationToken(principal, "N/A", principal.getAuthorities());
        getContext().setAuthentication(auth);
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("모든 계층의 메시지 생성 프로세스")
    void createMessageIntegration() {
        // given
        UserDto user = userService.create(
            new UserCreateRequest("tom", "tom@test.com", "pw123456"), Optional.empty()
        );
        authenticateAs(user, Role.USER);

        ChannelDto channel = channelService.create(
            new PublicChannelCreateRequest("public", "public channel")
        );

        MessageCreateRequest req = new MessageCreateRequest(
            "Hello, public channel!", channel.id(), user.id()
        );

        em.flush();
        em.clear();

        // when
        MessageDto dto = messageService.create(req, List.of());

        // then
        Message entity = messageRepository.findById(dto.id()).orElseThrow();
        assertThat(dto.content()).isEqualTo("Hello, public channel!");
        assertThat(dto.channelId()).isEqualTo(channel.id());
        assertThat(dto.author().id()).isEqualTo(user.id());

        assertThat(entity.getContent()).isEqualTo("Hello, public channel!");
        assertThat(entity.getChannel().getId()).isEqualTo(channel.id());
        assertThat(entity.getAuthor().getId()).isEqualTo(user.id());
    }

    @Test
    @DisplayName("모든 계층의 메시지 수정 프로세스")
    void updateMessageIntegration() {
        // given
        UserDto user = userService.create(
            new UserCreateRequest("tom", "tom@test.com", "pw123456"), Optional.empty()
        );
        authenticateAs(user, Role.USER);

        ChannelDto channel = channelService.create(
            new PublicChannelCreateRequest("public", "public channel")
        );
        MessageDto created = messageService.create(
            new MessageCreateRequest("test message", channel.id(), user.id()), List.of()
        );

        em.flush();
        em.clear();

        MessageUpdateRequest updateReq = new MessageUpdateRequest("updated message");

        // when
        MessageDto updated = messageService.update(created.id(), updateReq);

        // then
        Message entity = messageRepository.findById(updated.id()).orElseThrow();
        assertThat(updated.content()).isEqualTo("updated message");
        assertThat(entity.getContent()).isEqualTo("updated message");
    }

    @Test
    @DisplayName("모든 계층의 메시지 삭제 프로세스")
    void deleteMessageIntegration() {
        // given
        UserDto user = userService.create(
            new UserCreateRequest("tom", "tom@test.com", "pw123456"), Optional.empty()
        );
        authenticateAs(user, Role.USER);

        ChannelDto channel = channelService.create(
            new PublicChannelCreateRequest("public", "public channel")
        );
        MessageDto created = messageService.create(
            new MessageCreateRequest("test message", channel.id(), user.id()), List.of()
        );

        em.flush();
        em.clear();

        assertThat(messageRepository.existsByIdAndAuthor_Id(created.id(), user.id())).isTrue();

        // when
        messageService.delete(created.id());

        // then
        assertFalse(messageRepository.findById(created.id()).isPresent(),
            "메시지가 삭제되지 않았습니다: " + created.id());
    }
}