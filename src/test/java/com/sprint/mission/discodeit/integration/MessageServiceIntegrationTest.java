package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@DisplayName("MessageService 통합 테스트")
public class MessageServiceIntegrationTest {

    @Autowired private UserService userService;
    @Autowired private ChannelService channelService;
    @Autowired private MessageService messageService;
    @Autowired private MessageRepository messageRepository;

    @Test
    @DisplayName("모든 계층의 메시지 생성 프로세스")
    void createMessageIntegration() {
        // given
        UserDto user = userService.create(
            new UserCreateRequest("tom", "tom@test.com", "pw123456"), Optional.empty()
        );
        ChannelDto channel = channelService.create(
            new PublicChannelCreateRequest("public", "public channel")
        );

        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
            "Hello, public channel!", channel.id(), user.id()
        );

        // when
        MessageDto messageDto = messageService.create(messageCreateRequest, List.of());

        // then
        Message msgEntity = messageRepository.findById(messageDto.id()).orElseThrow();
        assertThat(messageDto.content()).isEqualTo("Hello, public channel!");
        assertThat(messageDto.channelId()).isEqualTo(channel.id());
        assertThat(messageDto.author().id()).isEqualTo(user.id());

        assertThat(msgEntity.getContent()).isEqualTo("Hello, public channel!");
        assertThat(msgEntity.getChannel().getId()).isEqualTo(channel.id());
        assertThat(msgEntity.getAuthor().getId()).isEqualTo(user.id());
    }

    @Test
    @DisplayName("모든 계층의 메시지 수정 프로세스")
    void updateMessageIntegration() {
        // given
        UserDto userDto = userService.create(
            new UserCreateRequest("tom", "tom@test.com", "pw123456"), Optional.empty()
        );
        ChannelDto channelDto = channelService.create(
            new PublicChannelCreateRequest("public", "public channel")
        );
        MessageDto messageDto = messageService.create(
            new MessageCreateRequest("test message", channelDto.id(), userDto.id()), List.of()
        );

        MessageUpdateRequest updateReq = new MessageUpdateRequest("updated message");

        // when
        MessageDto updatedMessageDto = messageService.update(messageDto.id(), updateReq);

        // then
        Message msgEntity = messageRepository.findById(updatedMessageDto.id()).orElseThrow();
        assertThat(updatedMessageDto.content()).isEqualTo("updated message");
        assertThat(msgEntity.getContent()).isEqualTo("updated message");
    }

    @Test
    @DisplayName("모든 계층의 메시지 삭제 프로세스")
    void deleteMessageIntegration() {
        // given
        UserDto userDto = userService.create(
            new UserCreateRequest("tom", "tom@test.com", "pw123456"), Optional.empty()
        );
        ChannelDto channelDto = channelService.create(
            new PublicChannelCreateRequest("public", "public channel")
        );
        MessageDto messageDto = messageService.create(
            new MessageCreateRequest("test message", channelDto.id(), userDto.id()), List.of()
        );

        // when
        messageService.delete(messageDto.id());

        // then
        assertFalse(messageRepository.findById(messageDto.id()).isPresent(),
            "메시지가 삭제되지 않았습니다: " + messageDto.id());
    }
}
