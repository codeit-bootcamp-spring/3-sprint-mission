package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        given(messageMapper.toResponse(any())).willAnswer(invocation -> {
            Message message = invocation.getArgument(0);
            return new MessageResponse(
                message.getId() != null ? message.getId() : UUID.randomUUID(),
                message.getCreatedAt() != null ? message.getCreatedAt() : Instant.now(),
                message.getUpdatedAt() != null ? message.getUpdatedAt() : Instant.now(),
                message.getContent(),
                message.getChannel().getId(),
                new UserDto(
                    message.getAuthor().getId() != null ? message.getAuthor().getId()
                        : UUID.randomUUID(),
                    message.getAuthor().getUsername(),
                    message.getAuthor().getEmail(),
                    null,
                    true
                ),
                List.of()
            );
        });
    }

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
        User author = new User("name", "email", "password", null);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(userId)).willReturn(Optional.of(author));
        given(binaryContentRepository.save(any())).willReturn(attachment);
        given(messageRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        MessageResponse response = messageService.create(request, List.of(attachmentReq));
        assertThat(response.content()).isEqualTo("hello");
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void shouldUpdateMessageSuccessfully() {
        UUID messageId = UUID.randomUUID();

        Channel channel = new Channel(ChannelType.PUBLIC, "test-channel", "description");
        User user = new User("tester", "tester@email.com", "password", null);
        Message message = new Message("old", channel, user, List.of());

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

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
            .isInstanceOf(MessageNotFoundException.class);
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void shouldDeleteMessageSuccessfully() {
        UUID messageId = UUID.randomUUID();

        Channel channel = new Channel(ChannelType.PUBLIC, "Test Channel",
            "Channel for deletion test");
        User user = new User("delete-user", "delete@test.com", "password", null);
        Message message = new Message("hi", channel, user, List.of());

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

        MessageResponse response = messageService.delete(messageId);
        assertThat(response.content()).isEqualTo("hi");
        then(messageRepository).should().delete(message);
    }

    @Test
    @DisplayName("존재하지 않는 메시지 삭제 예외 발생")
    void shouldThrowException_whenMessageNotFoundForDelete() {
        UUID id = UUID.randomUUID();
        given(messageRepository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.delete(id))
            .isInstanceOf(MessageNotFoundException.class);
    }

    @Test
    @DisplayName("채널 ID로 메시지 조회 성공")
    void shouldFindMessagesByChannelIdSuccessfully() {
        UUID channelId = UUID.randomUUID();
        Instant cursor = Instant.now();
        PageRequest pageRequest = PageRequest.of(0, 10);

        Channel channel = new Channel(ChannelType.PUBLIC, "Test Channel", "For message lookup");
        User user = new User("testuser", "test@email.com", "password", null);

        List<Message> messageList = List.of(
            new Message("m1", channel, user, List.of()),
            new Message("m2", channel, user, List.of())
        );
        SliceImpl<Message> slice = new SliceImpl<>(messageList);

        given(messageRepository.findAllByChannelIdWithAuthor(channelId, cursor,
            pageRequest)).willReturn(slice);
        given(pageResponseMapper.fromSlice(any(), any()))
            .willReturn(new PageResponse<>(List.of(), cursor, 10, false, 0L));

        PageResponse<MessageResponse> response = messageService.findAllByChannelId(channelId,
            cursor, pageRequest);

        assertThat(response).isNotNull();
        then(messageRepository).should()
            .findAllByChannelIdWithAuthor(channelId, cursor, pageRequest);
    }
}
