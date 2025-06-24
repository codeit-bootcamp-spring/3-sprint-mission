package com.sprint.mission.discodeit.integration;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.mission.discodeit.controller.MessageController;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@DisplayName("메세지 통합 테스트")
@ActiveProfiles("test")
@Transactional
public class MessageIntegrationTest {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageController messageController;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private BinaryContentStorage binaryContentStorage;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @Autowired
    private PageResponseMapper pageResponseMapper;


    @Test
    @DisplayName("메세지를 생성 할 수 있어야 한다")
    @Transactional
    void createMessage_Success() {
        // Given - 사용자와 채널 생성
        User user = new User("testuser", "test@example.com", "password", null);
        UserStatus userStatus = new UserStatus(user, Instant.now());
        userRepository.save(user);

        Channel channel = new Channel(ChannelType.PUBLIC, "test-channel", "test description");
        channelRepository.save(channel);

        MessageCreateRequest request = new MessageCreateRequest(
            "Hello, world!",
            channel.getId(),
            user.getId()
        );

        // When
        MessageDto result = messageController.create(request, Collections.emptyList()).getBody();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).isEqualTo("Hello, world!");
        assertThat(result.channelId()).isEqualTo(channel.getId());
        assertThat(result.author().id()).isEqualTo(user.getId());
        assertThat(result.author().username()).isEqualTo("testuser");
        assertThat(result.id()).isNotNull();
        assertThat(result.createdAt()).isNotNull();
        assertThat(result.attachments()).isEmpty();

        // Database 검증
        assertThat(messageRepository.count()).isEqualTo(1);
        Message savedMessage = messageRepository.findAll().get(0);
        assertThat(savedMessage.getContent()).isEqualTo("Hello, world!");
        assertThat(savedMessage.getChannel().getId()).isEqualTo(channel.getId());
        assertThat(savedMessage.getAuthor().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("메시지 생성할때 존재하지 않는 채널로 생성하면 실패해야한다")
    @Transactional
    void createMessage_ChannelNotFound_Fail() {
        // Given - 사용자만 생성 (채널은 생성하지 않음)
        User user = new User("testuser", "test@example.com", "password", null);
        UserStatus userStatus = new UserStatus(user, Instant.now());
        userRepository.save(user);

        UUID nonExistentChannelId = UUID.randomUUID();
        MessageCreateRequest request = new MessageCreateRequest(
            "Hello, world!",
            nonExistentChannelId,
            user.getId()
        );

        // When & Then
        assertThatThrownBy(() -> messageController.create(request, Collections.emptyList()))
            .isInstanceOf(ChannelNotFoundException.class);

        // Database 검증
        assertThat(messageRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("메시지 목록 조회를 할 수 있어야한다")
    @Transactional
    void findMessagesByChannelId_Success() {
        // Given - 사용자, 채널, 메시지들 생성
        User user = new User("testuser", "test@example.com", "password", null);
        UserStatus userStatus = new UserStatus(user, Instant.now());
        userRepository.save(user);

        Channel channel = new Channel(ChannelType.PUBLIC, "test-channel", "test description");
        channelRepository.save(channel);

        Message message1 = new Message("First message", channel, user, Collections.emptyList());
        Message message2 = new Message("Second message", channel, user, Collections.emptyList());
        messageRepository.save(message1);
        messageRepository.save(message2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        PageResponse<MessageDto> result = messageController.findAllByChannelId(
            channel.getId(),
            Instant.now(),
            pageable
        ).getBody();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content())
            .extracting(MessageDto::content)
            .containsExactlyInAnyOrder("First message", "Second message");
        assertThat(result.content().get(0).author().username()).isEqualTo("testuser");

        // Database 검증
        assertThat(messageRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("메세지 목록을 조회할때 메세지가 없으면 빈 목록을 리턴한다")
    @Transactional
    void findMessagesByChannelId_EmptyList() {
        // Given - 채널만 생성 (메시지는 없음)
        Channel channel = new Channel(ChannelType.PUBLIC, "test-channel", "test description");
        channelRepository.save(channel);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        PageResponse<MessageDto> result = messageController.findAllByChannelId(
            channel.getId(),
            Instant.now(),
            pageable
        ).getBody();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).isEmpty();
        assertThat(result.hasNext()).isFalse();

        // Database 검증
        assertThat(messageRepository.count()).isEqualTo(0);
    }

}
