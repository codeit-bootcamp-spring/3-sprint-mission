package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageMapper messageMapper;


    @InjectMocks
    private BasicMessageService messageService;

    @Test
    @DisplayName("메시지 생성 - 성공")
    void create_Success() {
        // Given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        String content = "Hello, World!";
        MessageCreateRequest request = new MessageCreateRequest(content, channelId, authorId);
        List<BinaryContentCreateRequest> attachments = Collections.emptyList();

        Channel channel = new Channel(ChannelType.PUBLIC, "Test Channel", "Description");
        User author = new User("testuser", "test@example.com", "password", null);
        Message savedMessage = new Message(content, null, null, Collections.emptyList());
        MessageDto expectedDto = new MessageDto(messageId, Instant.now(), null, content, channelId,
            new UserDto(authorId, null, null, null, null), Collections.emptyList());

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));
        given(messageRepository.save(any(Message.class))).willReturn(savedMessage);
        given(messageMapper.toDto(any(Message.class))).willReturn(expectedDto);

        // When
        MessageDto result = messageService.create(request, attachments);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        then(channelRepository).should().findById(channelId);
        then(userRepository).should().findById(authorId);
        then(messageRepository).should().save(any(Message.class));
        then(messageMapper).should().toDto(any(Message.class));
    }

    @Test
    @DisplayName("메시지 생성 - 채널을 찾을 수 없어 실패")
    void create_FailWhenChannelNotFound() {
        // Given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        MessageCreateRequest request = new MessageCreateRequest("Hello", channelId, authorId);
        List<BinaryContentCreateRequest> attachments = Collections.emptyList();

        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> messageService.create(request, attachments))
            .isInstanceOf(ChannelNotFoundException.class);

        then(channelRepository).should().findById(channelId);
        then(userRepository).should(never()).findById(authorId);
        then(messageRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("메시지 생성 - 사용자를 찾을 수 없어 실패")
    void create_FailWhenUserNotFound() {
        // Given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        MessageCreateRequest request = new MessageCreateRequest("Hello", channelId, authorId);
        List<BinaryContentCreateRequest> attachments = Collections.emptyList();

        Channel channel = new Channel(ChannelType.PUBLIC, "Test Channel", "Description");

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> messageService.create(request, attachments))
            .isInstanceOf(UserNotFoundException.class);

        then(channelRepository).should().findById(channelId);
        then(userRepository).should().findById(authorId);
        then(messageRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("메시지 수정 - 성공")
    void update_Success() {
        // Given
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        String newContent = "Updated message content";
        MessageUpdateRequest request = new MessageUpdateRequest(newContent);

        Message existingMessage = new Message("Old content", null, null, Collections.emptyList());
        MessageDto expectedDto = new MessageDto(messageId, Instant.now(), null, newContent,
            channelId, new UserDto(authorId, null, null, null, null), Collections.emptyList());

        given(messageRepository.findById(messageId)).willReturn(Optional.of(existingMessage));
        given(messageMapper.toDto(existingMessage)).willReturn(expectedDto);

        // When
        MessageDto result = messageService.update(messageId, request);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        then(messageRepository).should().findById(messageId);
        then(messageMapper).should().toDto(existingMessage);
    }

    @Test
    @DisplayName("메시지 수정 - 메시지를 찾을 수 없어 실패")
    void update_FailWhenMessageNotFound() {
        // Given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("Updated content");

        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> messageService.update(messageId, request))
            .isInstanceOf(MessageNotFoundException.class);

        then(messageRepository).should().findById(messageId);
        then(messageMapper).should(never()).toDto(any());
    }

    @Test
    @DisplayName("메시지 삭제 - 성공")
    void delete_Success() {
        // Given
        UUID messageId = UUID.randomUUID();

        given(messageRepository.existsById(messageId)).willReturn(true);

        // When
        messageService.delete(messageId);

        // Then
        then(messageRepository).should().existsById(messageId);
        then(messageRepository).should().deleteById(messageId);
    }
}