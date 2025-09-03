package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
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
class BasicMessageServiceTest {

    @InjectMocks
    private BasicMessageService messageService;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private PageResponseMapper pageResponseMapper;

    @Test
    @DisplayName("메시지 생성 성공")
    void shouldCreateMessageSuccessfully() {
        UUID channelId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        MessageCreateRequest request = new MessageCreateRequest("hello", channelId, userId);
        BinaryContentCreateRequest attachmentReq = new BinaryContentCreateRequest("file.png",
            "image/png", new byte[10]);
        BinaryContent attachment = new BinaryContent("file.png", 10L, "image/png");

        Channel channel = new Channel(ChannelType.PUBLIC, "test", null);
        User author = new User("name", "email", "password", null, Role.USER);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(userId)).willReturn(Optional.of(author));
        given(binaryContentRepository.save(any())).willReturn(attachment);
        given(messageRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        given(messageMapper.toResponse(any())).willAnswer(invocation -> {
            Message message = invocation.getArgument(0);
            return new MessageResponse(
                UUID.randomUUID(),
                Instant.now(),
                Instant.now(),
                message.getContent(),
                channelId,
                new UserDto(UUID.randomUUID(), author.getUsername(), author.getEmail(), null, true,
                    Role.USER),
                List.of()
            );
        });

        MessageResponse response = messageService.create(request, List.of(attachmentReq));
        assertThat(response.content()).isEqualTo("hello");
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void shouldUpdateMessageSuccessfully() {
        UUID messageId = UUID.randomUUID();

        Channel channel = new Channel(ChannelType.PUBLIC, "test-channel", "description");
        User user = new User("tester", "tester@email.com", "password", null, Role.USER);
        Message message = new Message("old", channel, user, List.of());

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

        given(messageMapper.toResponse(any())).willAnswer(invocation -> {
            Message updatedMessage = invocation.getArgument(0);
            return new MessageResponse(
                messageId,
                Instant.now(),
                Instant.now(),
                updatedMessage.getContent(),
                channel.getId(),
                new UserDto(UUID.randomUUID(), user.getUsername(), user.getEmail(), null, true,
                    Role.USER),
                List.of()
            );
        });

        MessageUpdateRequest request = new MessageUpdateRequest("updated");
        MessageResponse response = messageService.update(messageId, request);

        assertThat(response.content()).isEqualTo("updated");
    }

    @Test
    @DisplayName("존재하지 않는 메시지 수정 시 예외 발생")
    void shouldThrowException_whenMessageNotFoundForUpdate() {
        UUID messageId = UUID.randomUUID();
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.update(messageId, new MessageUpdateRequest("x")))
            .isInstanceOf(MessageNotFoundException.class)
            .hasMessageContaining("Message with ID");
    }

    @Test
    @DisplayName("존재하지 않는 메시지 삭제 예외 발생")
    void shouldThrowException_whenMessageNotFoundForDelete() {
        UUID id = UUID.randomUUID();
        given(messageRepository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.delete(id))
            .isInstanceOf(MessageNotFoundException.class)
            .hasMessageContaining("Message with ID");
    }
}
