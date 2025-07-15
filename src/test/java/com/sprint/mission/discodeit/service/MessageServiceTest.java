package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("메세지 서비스 단위 테스트")
public class MessageServiceTest {

    @Mock UserRepository userRepository;
    @Mock ChannelRepository channelRepository;
    @Mock BinaryContentRepository binaryContentRepository;
    @Mock BinaryContentStorage binaryContentStorage;
    @Mock MessageRepository messageRepository;
    @Mock MessageMapper messageMapper;
    @Mock PageResponseMapper pageResponseMapper;

    @InjectMocks private BasicMessageService messageService;


    private User user;
    private Channel channel;

    @BeforeEach
    void setUp() {
        user = new User("테스트유저", "test@gmail.com", "00000000", null);
        channel = new Channel(ChannelType.PRIVATE, "테스트 채널", "테스트 채널");
    }

    @Test
    @DisplayName("정상적인 요청을 통해 메세지를 등록할 수 있다")
    void shouldCreateMessageWhenRequestValid() {
        // given
        UUID authorId = user.getId();
        UUID channelId = channel.getId();

        MessageCreateRequest messageCreateRequest = new MessageCreateRequest("테스트", authorId, channelId);

        BinaryContentCreateRequest req1 = new BinaryContentCreateRequest("file1", "png", "bytes".getBytes());
        BinaryContentCreateRequest req2 = new BinaryContentCreateRequest("file2", "jpg", "more".getBytes());
        BinaryContentCreateRequest req3 = new BinaryContentCreateRequest("file3", "gif", "data".getBytes());

        BinaryContentDto dto1 = new BinaryContentDto(UUID.randomUUID(), "file1", 10L, "file1", "test1".getBytes());
        BinaryContentDto dto2 = new BinaryContentDto(UUID.randomUUID(), "file2", 10L, "file2", "test2".getBytes());
        BinaryContentDto dto3 = new BinaryContentDto(UUID.randomUUID(), "file3", 10L, "file3", "test3".getBytes());

        Message message = new Message("테스트", channel, user);
        MessageDto messageDto = new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(), "테스트", channelId, null, List.of(dto1, dto2, dto3));

        given(userRepository.findById(authorId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(binaryContentRepository.save(any(BinaryContent.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        given(messageRepository.save(any(Message.class))).willReturn(message);
        given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

        // when
        MessageDto result = messageService.create(messageCreateRequest, List.of(req1, req2, req3));

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).isEqualTo("테스트");
        assertThat(result.attachments().size()).isEqualTo(3);
        assertThat(result.attachments().get(0)).isEqualTo(dto1);

        verify(binaryContentRepository, times(3)).save(any());
        verify(binaryContentStorage, times(3)).put(any(), any());
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    @DisplayName("존재하지 않는 채널 ID로 메세지 생성 요청 시 예외 발생")
    void shouldNotCreateMessageWhenNotFoundChannel() {
        // given
        UUID authorId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID channelId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest("테스트", authorId, channelId);

        BinaryContentCreateRequest req1 = new BinaryContentCreateRequest("file1", "png", "bytes".getBytes());
        BinaryContentCreateRequest req2 = new BinaryContentCreateRequest("file2", "jpg", "more".getBytes());
        BinaryContentCreateRequest req3 = new BinaryContentCreateRequest("file3", "gif", "data".getBytes());

        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when * then
        assertThatThrownBy(() -> messageService.create(messageCreateRequest, List.of(req1, req2, req3)))
                .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("정상적인 요청을 통해 메세지를 수정할 수 있다")
    void shouldUpdateMessageWhenRequestValid() {
        // given
        UUID authorId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID channelId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID messageId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        ReflectionTestUtils.setField(user, "id", authorId);
        ReflectionTestUtils.setField(channel, "id", channelId);

        MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest("newContent");

        UserDto userDto = new UserDto(UUID.randomUUID(), "test", "test@gmail.com", null, null);
        Message message = new Message("test", channel, user);
        ReflectionTestUtils.setField(message, "id", messageId);

        MessageDto messageDto = new MessageDto(UUID.randomUUID(), null, null, "newContent", channelId, userDto, null);

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
        given(messageRepository.save(any(Message.class))).willReturn(message);
        given(messageMapper.toDto(message)).willReturn(messageDto);
        // when

        MessageDto result = messageService.update(messageId, messageUpdateRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).isEqualTo("newContent");
    }

    @Test
    @DisplayName("존재하지 않는 메세지 ID로 수정 요청 시 예외 발생")
    void shouldNotUpdateMessageWhenNotFoundMessageId() {
        // given
        UUID messageId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest("newContent");

        given(messageRepository.findById(messageId)).willReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> messageService.update(messageId, messageUpdateRequest))
                .isInstanceOf(MessageNotFoundException.class);
    }

    @Test
    @DisplayName("정상적인 요청을 통해 메세지를 삭제할 수 있다")
    void shouldDeleteMessageWhenRequestValid() {
        // given
        UUID messageId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID attachmentId = UUID.randomUUID();
        BinaryContent attachment = new BinaryContent("file", 100L, "png");
        ReflectionTestUtils.setField(attachment, "id", attachmentId); // UUID 세팅

        Message message = new Message("test", channel, user);
        message.addAttachment(attachment);
        ReflectionTestUtils.setField(message, "id", messageId);

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

        // when
        messageService.delete(messageId);

        // then
        verify(binaryContentRepository).deleteById(attachmentId); // <- 실제 첨부파일 ID로
        verify(messageRepository).deleteById(messageId);
    }

    @Test
    @DisplayName("존재하지 않는 메세지 ID로 삭제 요청 시 예외 발생")
    void shouldNotDeleteMessageWhenNotFoundMessageId() {
        // given
        UUID messageId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        given(messageRepository.findById(messageId)).willReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> messageService.delete(messageId))
                .isInstanceOf(MessageNotFoundException.class);
    }

    @Test
    @DisplayName("정상적인 요청을 통해 채널의 메세지 목록을 조회할 수 있다")
    void shouldFindMessageListByChannelIdWhenRequestValid() {
        // given
        UUID channelId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        Instant cursor = null;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        Message message1 = new Message("msg1", channel, user);
        Message message2 = new Message("msg2", channel, user);

        List<Message> messages = List.of(message1, message2);
        Slice<Message> slice = new SliceImpl<>(messages, pageable, false);

        List<MessageDto> messageDtos = messages.stream()
                .map(msg -> new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(), msg.getContent(), channelId, null, null))
                .toList();

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(messageRepository.findByChannelIdOrderByCreatedAtDesc(channelId, pageable))
                .willReturn(slice);
        given(messageMapper.toDto(any())).willAnswer(invocation -> {
            Message msg = invocation.getArgument(0);
            return new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(), msg.getContent(), channelId, null, null);
        });

        given(pageResponseMapper.fromSlice(any(Slice.class)))
                .willReturn(new PageResponse<>(messageDtos, null, messageDtos.size(), false, (long) messageDtos.size()));

        // when
        PageResponse<?> result = messageService.findAllByChannelId(channelId, cursor, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(2);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.content())
                .extracting("content")
                .containsExactly("msg1", "msg2");
    }

    @Test
    @DisplayName("존재하지 않는 채널 ID로 메세지 목록 조회 시 예외 발생")
    void shouldFindMessageListByChannelIdWhenNotFoundChannelId() {
        // given
        UUID channelId = UUID.randomUUID();
        Instant cursor = null;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());


        given(channelRepository.findById(channelId)).willReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> messageService.findAllByChannelId(channelId, cursor, pageable))
                .isInstanceOf(ChannelNotFoundException.class);
    }
}
