package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
@DisplayName("메시지 서비스 단위 테스트")
public class MessageServiceTest {

    @Mock private MessageRepository messageRepository;
    @Mock private ChannelRepository channelRepository;
    @Mock private UserRepository userRepository;
    @Mock private BinaryContentRepository binaryContentRepository;
    @Mock private BinaryContentStorage binaryContentStorage;
    @Mock private MessageMapper messageMapper;
    @Mock private PageResponseMapper pageResponseMapper;

    @InjectMocks
    private BasicMessageService messageService;

    private UUID authorId;
    private UUID channelId;
    private UUID messageId;
    private User user;
    private Channel channel;
    private MessageCreateRequest messageCreateRequest;

    @BeforeEach
    void setUp() {
        authorId  = UUID.randomUUID();
        channelId = UUID.randomUUID();
        messageId = UUID.randomUUID();
        user = new User("tom", "tom@test.com", "pw123456", null);
        channel = new Channel(ChannelType.PUBLIC, "public", "public");
        messageCreateRequest = new MessageCreateRequest("test message", channelId, authorId);
    }

    @Test
    @DisplayName("메시지 생성 성공")
    void createMessage() {
        // given
        UserDto authorDto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), null, false, Role.USER);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(user));

        byte[] data = "bytes".getBytes();
        BinaryContentCreateRequest attachmentRequest = new BinaryContentCreateRequest("attach.png", "image/png", data);
        BinaryContent binaryContent = new BinaryContent("attach.png", (long) data.length, "image/png");
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(binaryContent);
        given(binaryContentStorage.put(binaryContent.getId(), data)).willReturn(binaryContent.getId());

        Message message = new Message("test message", channel, user, List.of(binaryContent));
        given(messageRepository.save(any(Message.class))).willReturn(message);

        MessageDto messageDto = new MessageDto(
            message.getId(),
            Instant.MIN,
            Instant.MIN,
            "test message",
            channelId,
            authorDto,
            List.of(new BinaryContentDto(
                binaryContent.getId(),
                binaryContent.getFileName(),
                binaryContent.getSize(),
                binaryContent.getContentType()
            ))
        );
        given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

        // when
        MessageDto result = messageService.create(messageCreateRequest, List.of(attachmentRequest));

        // then
        then(channelRepository).should().findById(channelId);
        then(userRepository).should().findById(authorId);
        then(binaryContentRepository).should().save(any(BinaryContent.class));
        then(binaryContentStorage).should().put(binaryContent.getId(), data);
        then(messageRepository).should().save(any(Message.class));
        then(messageMapper).should().toDto(any(Message.class));

        assertThat(result).isSameAs(messageDto);
    }

    @Test
    @DisplayName("메시지 생성 중 ChannelNotFoundException 예외 발생")
    void createMessageWithChannelNotFoundException() {
        // given
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> messageService.create(messageCreateRequest, List.of()))
            .isInstanceOf(ChannelNotFoundException.class);

        then(channelRepository).should().findById(channelId);
        then(userRepository).should(never()).findById(any());
        then(binaryContentRepository).should(never()).save(any());
        then(binaryContentStorage).should(never()).put(any(), any());
        then(messageRepository).should(never()).save(any());
        then(messageMapper).should(never()).toDto(any());
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void updateMessage() {
        // given
        MessageUpdateRequest request = new MessageUpdateRequest("update message");
        Message message = new Message("origin message", channel, user, List.of());
        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

        MessageDto messageDto = new MessageDto(
            message.getId(),
            Instant.MIN,
            Instant.MIN,
            "update message",
            message.getChannel().getId(),
            new UserDto(message.getAuthor().getId(),
                message.getAuthor().getUsername(),
                message.getAuthor().getEmail(),
                null, false, Role.USER),
            List.of()
        );
        given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

        // when
        MessageDto result = messageService.update(messageId, request);

        // then
        then(messageRepository).should().findById(messageId);
        assertThat(message.getContent()).isEqualTo("update message");
        then(messageMapper).should().toDto(message);
        assertThat(result).isSameAs(messageDto);
    }

    @Test
    @DisplayName("메시지 수정 중 MessageNotFoundException 예외 발생")
    void updateMessageWithMessageNotFoundException() {
        // given
        MessageUpdateRequest request = new MessageUpdateRequest("update message");
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> messageService.update(messageId, request))
            .isInstanceOf(MessageNotFoundException.class);

        then(messageMapper).should(never()).toDto(any());
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void deleteMessage() {
        // given
        BinaryContent attachment1 = new BinaryContent("img1.png", 100L, "image/png");
        BinaryContent attachment2 = new BinaryContent("img2.jpg", 200L, "image/jpeg");
        List<BinaryContent> attachments = List.of(attachment1, attachment2);

        Message message = new Message("test message", channel, user, attachments);

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

        willDoNothing().given(binaryContentRepository).deleteAll(attachments);
        willDoNothing().given(messageRepository).delete(message);

        // when
        messageService.delete(messageId);

        // then
        then(messageRepository).should().findById(messageId);
        then(binaryContentRepository).should().deleteAll(attachments);
        then(messageRepository).should().delete(message);
    }

    @Test
    @DisplayName("메시지 삭제 중 MessageNotFoundException 예외 발생")
    void deleteMessageWithMessageNotFoundException() {
        // given
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> messageService.delete(messageId))
            .isInstanceOf(MessageNotFoundException.class);

        then(binaryContentRepository).should(never()).deleteAll(any());
        then(messageRepository).should(never()).delete(any());
    }

    @Test
    @DisplayName("특정 채녈의 메시지 목록 조회 성공")
    void findAllByChannelId() {
        // given
        int pageSize = 2;
        Pageable requestPage = PageRequest.of(0, pageSize, Sort.by("createdAt").descending());
        Pageable cursorPage  = PageRequest.of(0, pageSize+1, Sort.by("createdAt").descending());

        Message m1 = new Message("msg1", channel, user, List.of());
        Message m2 = new Message("msg2", channel, user, List.of());
        Slice<Message> slice = new SliceImpl<>(List.of(m1, m2), cursorPage, false);
        given(messageRepository.findAllByChannelId(channelId, cursorPage)).willReturn(slice);

        Instant t1 = Instant.parse("2025-06-01T00:00:00Z");
        Instant t2 = Instant.parse("2025-06-02T00:00:00Z");
        MessageDto d1 = new MessageDto(m1.getId(), t1, t1, "msg1", channelId,
            new UserDto(user.getId(), user.getUsername(), user.getEmail(), null, false, Role.USER),
            List.of());
        MessageDto d2 = new MessageDto(m2.getId(), t2, t2, "msg2", channelId,
            new UserDto(user.getId(), user.getUsername(), user.getEmail(), null, false, Role.USER),
            List.of());
        given(messageMapper.toDto(m1)).willReturn(d1);
        given(messageMapper.toDto(m2)).willReturn(d2);

        PageResponse<MessageDto> expected = new PageResponse<>(
             List.of(d1, d2),
            null,
            pageSize,
            false,
            2L
        );
        given(pageResponseMapper.fromSlice(any(Slice.class), isNull())).willReturn(expected);

        // when
        PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, null, requestPage);

        // then
        then(messageRepository).should().findAllByChannelId(channelId, cursorPage);
        then(messageMapper).should().toDto(m1);
        then(messageMapper).should().toDto(m2);
        then(pageResponseMapper).should().fromSlice(any(Slice.class), isNull());
        assertThat(result).isSameAs(expected);
    }

    @Test
    @DisplayName("특정 채녈의 메시지 목록 조회 중 RuntimeException 예외 발생")
    void findAllByChannelIdWithRuntimeException() {
        // given
        int pageSize = 5;
        Pageable requestPage = PageRequest.of(0, pageSize, Sort.by("createdAt").descending());
        Pageable cursorPage  = PageRequest.of(0, pageSize + 1, Sort.by("createdAt").descending());
        given(messageRepository.findAllByChannelId(channelId, cursorPage)).willThrow(new RuntimeException("Database 접근 오류"));

        // when, then
        assertThatThrownBy(() -> messageService.findAllByChannelId(channelId, null, requestPage))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Database 접근 오류");

        then(messageMapper).should(never()).toDto(any());
        then(pageResponseMapper).should(never()).fromSlice(any(), any());
    }
}