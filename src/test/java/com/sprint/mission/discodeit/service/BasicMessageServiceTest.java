package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
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
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
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
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageService 단위 테스트")
public class BasicMessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private PageResponseMapper pageResponseMapper;

    @InjectMocks
    private BasicMessageService messageService;

    @Test
    @DisplayName("메시지 생성 - case : success")
    void createMessageSuccess() {
        // Given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        MessageCreateRequest request = new MessageCreateRequest("test", channelId, authorId);
        Channel channel = new Channel(ChannelType.PUBLIC,"TestChannel","TestChannel Description");
        User author = new User("testUser", "test@test.com","009874",null);
        BinaryContentCreateRequest file = new BinaryContentCreateRequest("file.txt","txt/plain","파일 내용".getBytes());
        BinaryContent savedFile = new BinaryContent(file.fileName(), (long) file.bytes().length, file.contentType());
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));
        given(binaryContentRepository.save(any())).willReturn(savedFile);
        UUID messageId = UUID.randomUUID();
        MessageDto messageDto = new MessageDto(
            messageId,
            Instant.now(),
            null,
            "test",
            null,
            null,
            null);
        given(messageMapper.toDto(any())).willReturn(messageDto);

        // When
        MessageDto result = messageService.create(request, List.of(file));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).isEqualTo("test");
        then(channelRepository).should().findById(channelId);
        then(userRepository).should().findById(authorId);
        then(messageRepository).should().save(any());
        then(messageMapper).should().toDto(any());
    }

    @Test
    @DisplayName("메시지 생성 - case : 채널이 없는 상황으로 인한 failed")
    void createMessageFail() {
        // Given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        MessageCreateRequest request = new MessageCreateRequest("test", channelId, authorId);

        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // When
        ThrowingCallable when = () -> messageService.create(request, List.of());

        // Then
        assertThatThrownBy(when)
            .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("메시지 수정 - case : success")
    void updateMessageSuccess() {
        // Given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("newContent");
        Message message = mock(Message.class);
        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
        given(messageMapper.toDto(message)).willReturn(
            new MessageDto(messageId, Instant.now(), null, "newContent", null, null, null)
        );

        // When
        MessageDto result = messageService.update(messageId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).isEqualTo("newContent");
        verify(messageRepository, times(1)).findById(messageId);
        verify(message, times(1)).update("newContent");
        verify(messageMapper, times(1)).toDto(message);

    }

    @Test
    @DisplayName("메시지 수정 - case : 해당 메시지가 없음으로 인한 failed")
    void updateMessageFail() {
        // Given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("newContent");
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        // When
        ThrowingCallable when = () -> messageService.update(messageId, request);

        // Then
        assertThatThrownBy(when)
            .isInstanceOf(MessageNotFoundException.class);
    }

    @Test
    @DisplayName("채널 메시지 목록 조회 - case : success")
    void findAllByChannelIdSuccess() {
        // Given
        UUID channelId = UUID.randomUUID();
        Instant now = Instant.now();
        Pageable pageable = PageRequest.of(0, 10);
        User author = new User("testUserName", "test@test.com", "009874", null);
        Message message = new Message("test", new Channel(ChannelType.PUBLIC, "testChannel","testChannel Description"),author, List.of());
        MessageDto messageDto = new MessageDto(
            UUID.randomUUID(),
            Instant.now(),
            null,
            "test",
            channelId,
            null,
            null
        );
        Slice<Message> messageSlice = new SliceImpl<>(List.of(message), pageable, false);
        given(messageRepository.findAllByChannelIdWithAuthor(eq(channelId), any(), eq(pageable)))
            .willReturn(messageSlice);
        given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);
        given(pageResponseMapper.fromSlice(any(), any())).willReturn(
            new PageResponse<>(
                List.of(messageDto),
                null,
                1,
                false,
                1L
            )
        );

        // When
        PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, now, pageable);

        // Then
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).content()).isEqualTo("test");

    }

    @DisplayName("채널 메시지 목록 조회 - case : 메시지가 없어서 빈 결과인 경우")
    @Test
    void findAllByChannelIdFail() {
        // Given
        UUID channelId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 5);
        Slice<Message> emptySlice = new SliceImpl<>(List.of(), pageable, false);
        given(messageRepository.findAllByChannelIdWithAuthor(eq(channelId), any(), eq(pageable)))
            .willReturn(emptySlice);
        given(pageResponseMapper.fromSlice(any(), any())).willReturn(
            new PageResponse<>(
                List.of(),
                null,
                0,
                false,
                0L
            )
        );

        // When
        PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, null, pageable);

        // Then
        assertThat(result.content()).isEmpty();
        then(messageRepository).should()
            .findAllByChannelIdWithAuthor(eq(channelId), any(), eq(pageable));
        then(pageResponseMapper).should().fromSlice(any(), any());
    }

    @Test
    @DisplayName("메시지 삭제 - case : success")
    void deleteMessageSuccess() {
        // Given
        UUID messageId = UUID.randomUUID();
        given(messageRepository.existsById(messageId)).willReturn(true);

        // When
        assertDoesNotThrow(() -> messageService.delete(messageId));

        // Then
        verify(messageRepository, times(1)).existsById(messageId);
        verify(messageRepository, times(1)).deleteById(messageId);
    }

    @Test
    @DisplayName("메시지 삭제 - case : 존재하지 않는 메시지 삭제로 인한 failed")
    void deleteMessageFailWithNotFound() {
        // Given
        UUID messageId = UUID.randomUUID();
        given(messageRepository.existsById(messageId)).willReturn(false);

        // When
        ThrowingCallable when = () -> messageService.delete(messageId);

        // Then
        assertThatThrownBy(when)
            .isInstanceOf(MessageNotFoundException.class)
            .hasMessageContaining("메시지를 찾을 수 없습니다.");

        verify(messageRepository, times(1)).existsById(messageId);
        verify(messageRepository, never()).deleteById(messageId);
    }
}
