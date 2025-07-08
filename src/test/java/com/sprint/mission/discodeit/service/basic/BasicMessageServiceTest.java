package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;

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
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageService 단위 테스트")
class BasicMessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @Mock
    private PageResponseMapper pageResponseMapper;

    @InjectMocks
    private BasicMessageService messageService;

    @Test
    @DisplayName("메시지 생성 - 성공(첨부파일 없음)")
    void create_NoAttachments_Success() {
        // Given
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        User author = new User("조현아", "zzo@email.com", "password123!", null);
        ReflectionTestUtils.setField(author, "id", authorId);
        UserDto userDto = new UserDto(authorId, "조현아", "zzo@email.com", null, null);
        Channel channel = new Channel(ChannelType.PUBLIC, "채널1", "테스트 채널");
        ReflectionTestUtils.setField(channel, "id", channelId);
        MessageCreateRequest request = new MessageCreateRequest("테스트 메시지 내용~", channelId, authorId);
        Message message = new Message(request.content(), channel, author, null);
        MessageDto messageDto = new MessageDto(UUID.randomUUID(), Instant.now(), null,
            request.content(), channelId, userDto, null);
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));
        given(messageRepository.save(any(Message.class))).willReturn(message);
        given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

        // When
        MessageDto result = messageService.create(request, null);

        // Then
        assertThat(result.content()).isEqualTo(message.getContent());
        then(channelRepository).should().findById(channelId);
        then(userRepository).should().findById(authorId);
        then(messageRepository).should().save(any(Message.class));
        then(messageMapper).should().toDto(any(Message.class));
    }

    @Test
    @DisplayName("메시지 생성 - 성공(첨부파일만)")
    void create_OnlyAttachments_Success() {
        // Given
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        byte[] attachByte = "testAttachment".getBytes();
        BinaryContent binaryContent = new BinaryContent("testAttachment",
            (long) attachByte.length,
            "png");
        BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
            "testAttachment", "png",
            attachByte);
        List<BinaryContent> attachments = List.of(binaryContent);
        BinaryContentDto attachmentDto = new BinaryContentDto(UUID.randomUUID(),
            "testAttachment", (long) attachByte.length, "png");
        User author = new User("조현아", "zzo@email.com", "password123!", null);
        ReflectionTestUtils.setField(author, "id", authorId);
        UserDto userDto = new UserDto(authorId, "조현아", "zzo@email.com", null, null);
        Channel channel = new Channel(ChannelType.PUBLIC, "채널1", "테스트 채널");
        ReflectionTestUtils.setField(channel, "id", channelId);
        // 메시지 내용없고 첨부파일만
        MessageCreateRequest request = new MessageCreateRequest(null, channelId, authorId);
        Message message = new Message(request.content(), channel, author, attachments);
        MessageDto messageDto = new MessageDto(UUID.randomUUID(), Instant.now(), null,
            request.content(), channelId, userDto, List.of(attachmentDto));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(binaryContent);
        given(binaryContentStorage.put(any(), any())).willReturn(attachmentDto.id());
        given(messageRepository.save(any(Message.class))).willReturn(message);
        given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

        // When
        MessageDto result = messageService.create(request, List.of(binaryContentCreateRequest));

        // Then
        assertThat(result.content()).isEqualTo(message.getContent());
        then(channelRepository).should().findById(channelId);
        then(userRepository).should().findById(authorId);
        then(binaryContentRepository).should().save(any(BinaryContent.class));
        then(binaryContentStorage).should().put(any(), eq(attachByte));
        then(messageRepository).should().save(any(Message.class));
        then(messageMapper).should().toDto(any(Message.class));
    }

    @Test
    @DisplayName("메시지 전체 조회 - 성공")
    void findAllByChannelId_Success() {
        // Given
        Instant now = Instant.now();
        Pageable pageable = PageRequest.of(0, 10);
        UUID channelId = UUID.randomUUID();
        Message message = mock(Message.class);
        MessageDto messageDto = new MessageDto(
            UUID.randomUUID(),
            Instant.now(),
            null,
            "테스트 메시지 내용~",
            channelId,
            mock(UserDto.class),
            null
        );
        Slice<Message> messageSlice = new SliceImpl<>(List.of(message), pageable, false);
        PageResponse<MessageDto> expectedResponse = new PageResponse<>(
            List.of(messageDto), null, 1, false, 1L
        );
        given(messageRepository.findByChannelIdAndCreatedAtLessThan(eq(channelId), any(),
            eq(pageable)))
            .willReturn(messageSlice);
        given(messageMapper.toDto(message)).willReturn(messageDto);
        given(pageResponseMapper.fromSlice(any(Slice.class), any(Instant.class))).willReturn(
            expectedResponse);

        // When
        PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, now,
            pageable);

        // Then
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).content()).isEqualTo("테스트 메시지 내용~");
    }

    @Test
    @DisplayName("메시지 수정 - 성공")
    void update_Success() {
        // Given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("수정된 내용");
        Message message = mock(Message.class);
        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
        MessageDto messageDto = new MessageDto(messageId, Instant.now(), null, request.newContent(),
            null, null, null);
        given(messageMapper.toDto(message)).willReturn(messageDto);

        // When
        MessageDto result = messageService.update(messageId, request);

        // Then
        then(message).should().update("수정된 내용");
        assertThat(result.content()).isEqualTo("수정된 내용");
    }

    @Test
    @DisplayName("메시지 수정 - 실패(존재하지 않는 메시지)")
    void update_Fail_MessageNotFound() {
        // Given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("수정된 내용");
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> messageService.update(messageId, request));

        // Then
        assertThat(thrown)
            .isInstanceOf(MessageNotFoundException.class);
        then(messageRepository).should().findById(messageId);
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
        then(messageRepository).should().deleteById(messageId);
    }

    @Test
    @DisplayName("메시지 삭제 - 실패(존재하지 않는 메시지)")
    void delete_Fail_MessageNotFound() {
        // Given
        UUID messageId = UUID.randomUUID();
        given(messageRepository.existsById(messageId)).willReturn(false);

        // When
        Throwable thrown = catchThrowable(() -> messageService.delete(messageId));

        // Then
        assertThat(thrown)
            .isInstanceOf(MessageNotFoundException.class);
        then(messageRepository).should(never()).deleteById(any());
    }

}