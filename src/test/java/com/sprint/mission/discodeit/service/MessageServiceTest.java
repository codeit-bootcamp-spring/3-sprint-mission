package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
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
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageService 단위 테스트")
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    private BinaryContentStorage binaryContentStorage;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private PageResponseMapper pageResponseMapper;

    @InjectMocks
    private BasicMessageService messageService;

    @BeforeEach
    @DisplayName("테스트 환경 설정 확인")
    void setUp() {
        assertNotNull(messageRepository);
        assertNotNull(channelRepository);
        assertNotNull(userRepository);
        assertNotNull(messageMapper);
        assertNotNull(binaryContentStorage);
        assertNotNull(binaryContentRepository);
        assertNotNull(pageResponseMapper);
        assertNotNull(messageService);
    }

    @Nested
    @DisplayName("메시지 생성 테스트")
    class MessageCreateTests {

        @Test
        @DisplayName("메시지 생성 성공 - 첨부파일 없음")
        void createMessage_Success_WithoutAttachments() {
            //Given
            UUID channelId = UUID.randomUUID();
            UUID authorId = UUID.randomUUID();
            String content = "test message";

            MessageCreateRequest request = new MessageCreateRequest(content, channelId, authorId);

            Channel channel = new Channel(ChannelType.PUBLIC, "test channel", "description");
            User author = new User("testuser", "test@example.com", "pass", null);
            UserDto userDto = new UserDto(authorId, "testuser", "test@example.com", null, true);
            Message savedMessage = new Message(content, channel, author, null);
            MessageDto expectedDto = new MessageDto(UUID.randomUUID(), Instant.now(), null, content,
                channelId, userDto, null);

            given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
            given(userRepository.findById(authorId)).willReturn(Optional.of(author));
            given(messageRepository.save(any(Message.class))).willReturn(savedMessage);
            given(messageMapper.toDto(any(Message.class))).willReturn(expectedDto);

            // When
            MessageDto result = messageService.create(request, null);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.content()).isEqualTo(content);

            then(channelRepository).should().findById(channelId);
            then(userRepository).should().findById(authorId);
            then(messageRepository).should().save(any(Message.class));
            then(messageMapper).should().toDto(any(Message.class));
            then(binaryContentRepository).shouldHaveNoInteractions();
            then(binaryContentStorage).shouldHaveNoInteractions();

        }

        @Test
        @DisplayName("메시지 생성 실패 - 채널 없음")
        void createMessage_Fail_ChannelNotFound() {
            // Given
            UUID channelId = UUID.randomUUID();
            UUID authorId = UUID.randomUUID();
            MessageCreateRequest request = new MessageCreateRequest("test content", channelId,
                authorId);

            given(channelRepository.findById(channelId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> messageService.create(request, List.of()))
                .isInstanceOf(ChannelNotFoundException.class);

            then(channelRepository).should().findById(channelId);
            then(messageRepository).should(never()).save(any(Message.class));
            then(userRepository).shouldHaveNoInteractions();
            then(binaryContentRepository).shouldHaveNoInteractions();
            then(binaryContentStorage).shouldHaveNoInteractions();
            then(messageMapper).shouldHaveNoInteractions();

        }

        @Test
        @DisplayName("메시지 생성 실패 - 작성자 없음")
        void createMessage_Fail_AuthorNotFound() {
            // Given
            UUID channelId = UUID.randomUUID();
            UUID authorId = UUID.randomUUID();
            MessageCreateRequest request = new MessageCreateRequest("content", channelId, authorId);

            Channel channel = new Channel(ChannelType.PUBLIC, "test channel", "description");

            given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
            given(userRepository.findById(authorId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> messageService.create(request, List.of()))
                .isInstanceOf(UserNotFoundException.class);

            then(channelRepository).should().findById(channelId);
            then(userRepository).should().findById(authorId);
            then(messageRepository).should(never()).save(any(Message.class));
            then(binaryContentRepository).shouldHaveNoInteractions();
            then(binaryContentStorage).shouldHaveNoInteractions();
            then(messageMapper).shouldHaveNoInteractions();
        }

    }

    @Nested
    @DisplayName("메시지 수정 테스트")
    class MessageUpdateTests {

        @Test
        @DisplayName("메시지 수정 성공")
        void updateMessage_Success() {
            // Given
            UUID channelId = UUID.randomUUID();
            UUID messageId = UUID.randomUUID();
            String newContent = "수정된 메시지 내용";
            MessageUpdateRequest request = new MessageUpdateRequest(newContent);

            UserDto userDto = mock(UserDto.class);
            Message existingMessage = new Message("기존 내용", null, null, List.of());
            MessageDto expectedDto = new MessageDto(messageId, Instant.now(), null, newContent,
                channelId, userDto, null);

            given(messageRepository.findById(messageId)).willReturn(Optional.of(existingMessage));
            given(messageMapper.toDto(existingMessage)).willReturn(expectedDto);

            // When
            MessageDto result = messageService.update(messageId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.content()).isEqualTo(newContent);
            assertThat(result.channelId()).isEqualTo(channelId);
            assertThat(result.attachments()).isNullOrEmpty();

            then(messageRepository).should().findById(messageId);
            then(messageMapper).should().toDto(existingMessage);
        }

        @Test
        @DisplayName("메시지 수정 실패 - 메시지 없음")
        void updateMessage_Fail_MessageNotFound() {
            // Given
            UUID messageId = UUID.randomUUID();
            MessageUpdateRequest request = new MessageUpdateRequest("새 내용");

            given(messageRepository.findById(messageId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> messageService.update(messageId, request))
                .isInstanceOf(MessageNotFoundException.class);

            then(messageRepository).should().findById(messageId);
            then(messageMapper).should(never()).toDto(any(Message.class));
        }

    }

    @Nested
    @DisplayName("메시지 삭제 테스트")
    class MessageDeleteTests {

        @Test
        @DisplayName("메시지 삭제 성공")
        void deleteMessage_Success() {
            // Given
            UUID messageId = UUID.randomUUID();

            given(messageRepository.existsById(messageId)).willReturn(true);

            // When
            messageService.delete(messageId);

            // Then
            then(messageRepository).should().existsById(messageId);
            then(messageRepository).should().deleteById(messageId);
        }

        @Test
        @DisplayName("메시지 삭제 실패 - 메시지 없음")
        void deleteMessage_Fail_MessageNotFound() {
            // Given
            UUID messageId = UUID.randomUUID();

            given(messageRepository.existsById(messageId)).willReturn(false);

            // When & Then
            assertThatThrownBy(() -> messageService.delete(messageId))
                .isInstanceOf(MessageNotFoundException.class);

            then(messageRepository).should().existsById(messageId);
            then(messageRepository).should(never()).deleteById(messageId);
        }
    }

    @DisplayName("채널 ID로 메시지 목록 조회 성공 - createdAt 파라미터 제공")
    @Test
    void findAllByChannelId_Success_WithCreatedAt() {
        // Given
        UUID channelId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2024-01-01T10:00:00Z");
        Pageable pageable = PageRequest.of(0, 10);

        Message message1 = new Message("첫 번째 메시지", mock(Channel.class), mock(User.class), null);
        Message message2 = new Message("두 번째 메시지", mock(Channel.class), mock(User.class), null);

        List<Message> messages = List.of(message1, message2);
        Slice<Message> messageSlice = new SliceImpl<>(messages, pageable, true);

        given(messageRepository.findAllByChannelIdWithAuthor(channelId, createdAt, pageable))
            .willReturn(messageSlice);

        MessageDto messageDto1 = new MessageDto(
            message1.getId(), Instant.parse("2024-01-01T08:00:00Z"), null,
            "첫 번째 메시지", channelId,
            mock(UserDto.class), List.of());

        MessageDto messageDto2 = new MessageDto(
            message2.getId(), Instant.parse("2024-01-02T08:00:00Z"), null,
            "두 번째 메시지", channelId,
            mock(UserDto.class), List.of());

        given(messageMapper.toDto(message1)).willReturn(messageDto1);
        given(messageMapper.toDto(message2)).willReturn(messageDto2);

        Slice<MessageDto> dtoSlice = new SliceImpl<>(List.of(messageDto1, messageDto2), pageable,
            true);
        Instant expectedNextCursor = messageDto2.createdAt();

        given(pageResponseMapper.fromSlice(dtoSlice, expectedNextCursor))
            .willReturn(new PageResponse<>(dtoSlice.getContent(), expectedNextCursor,
                dtoSlice.getNumberOfElements(), true, null));

        // When
        PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, createdAt,
            pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(2);
        assertThat(result.content().get(0).content()).isEqualTo("첫 번째 메시지");
        assertThat(result.content().get(1).content()).isEqualTo("두 번째 메시지");
        assertThat(result.nextCursor()).isEqualTo(expectedNextCursor);
        assertThat(result.hasNext()).isTrue();

        then(messageRepository).should()
            .findAllByChannelIdWithAuthor(channelId, createdAt, pageable);
        then(messageMapper).should().toDto(message1);
        then(messageMapper).should().toDto(message2);
        then(pageResponseMapper).should().fromSlice(dtoSlice, expectedNextCursor);
    }


}
