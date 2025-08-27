package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ValidationException;
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
import java.util.Arrays;
import java.util.Collections;
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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasicMessageService 단위 테스트")
public class BasicMessageServiceTest {

    @Mock
    private ChannelRepository channelRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private MessageRepository messageRepository;
    
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

    // 테스트용 공통 데이터
    private UUID userId;
    private UUID channelId;
    private UUID messageId;
    private UUID attachmentId;
    private User user;
    private Channel channel;
    private Message message;
    private MessageCreateRequest messageCreateRequest;
    private MessageUpdateRequest messageUpdateRequest;
    private MessageDto messageDto;
    private BinaryContent attachment;
    private BinaryContentCreateRequest attachmentRequest;
    private Instant now;

    @BeforeEach
    void setUp() {
        // Mock 객체 주입 확인
        assertNotNull(messageService);
        assertNotNull(channelRepository);
        assertNotNull(userRepository);
        assertNotNull(messageRepository);
        assertNotNull(binaryContentRepository);
        assertNotNull(binaryContentStorage);
        assertNotNull(messageMapper);
        assertNotNull(pageResponseMapper);

        // 테스트 데이터 초기화
        userId = UUID.randomUUID();
        channelId = UUID.randomUUID();
        messageId = UUID.randomUUID();
        attachmentId = UUID.randomUUID();
        now = Instant.now();

        user = new User("홍길동", "hong@test.com", "1234");
        ReflectionTestUtils.setField(user, "id", userId);

        channel = new Channel("테스트 채널", ChannelType.PUBLIC, "테스트 채널 설명");
        ReflectionTestUtils.setField(channel, "id", channelId);

        messageCreateRequest = new MessageCreateRequest(
                "안녕하세요! 테스트 메시지입니다.",
                channelId,
                userId
        );

        messageUpdateRequest = new MessageUpdateRequest("수정된 메시지 내용입니다.");

        attachment = new BinaryContent("test-file.jpg", 1000L, "image/jpeg");
        ReflectionTestUtils.setField(attachment, "id", attachmentId);

        byte[] fileBytes = "test file content".getBytes();
        attachmentRequest = new BinaryContentCreateRequest("test-file.jpg", "image/jpeg", fileBytes);

        message = new Message(channel, user, "안녕하세요! 테스트 메시지입니다.",
                Collections.singletonList(attachment));
        ReflectionTestUtils.setField(message, "id", messageId);
        ReflectionTestUtils.setField(message, "createdAt", now);
        ReflectionTestUtils.setField(message, "updatedAt", now);

        UserDto userDto = new UserDto(userId, "홍길동", "hong@test.com", null, false);
        BinaryContentDto attachmentDto = new BinaryContentDto(attachmentId, "test-file.jpg", 1000L, "image/jpeg");

        messageDto = new MessageDto(
                messageId,
                now,
                now,
                "안녕하세요! 테스트 메시지입니다.",
                channelId,
                userDto,
                List.of(attachmentDto)
        );
    }

    @Nested
    @DisplayName("메시지 생성 테스트")
    class CreateMessageTests {

        @Test
        @DisplayName("첨부파일 없이 메시지 생성 성공")
        void create_WithoutAttachments_Success() {
            // Given
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
            given(messageRepository.save(any(Message.class))).willReturn(message);
            given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

            // When
            MessageDto result = messageService.create(messageCreateRequest, Arrays.asList());

            // Then
            assertThat(result).isNotNull();
            assertThat(result.content()).isEqualTo(messageCreateRequest.content());
            assertThat(result.channelId()).isEqualTo(channelId);
            assertThat(result.author().id()).isEqualTo(userId);

            then(userRepository).should().findById(userId);
            then(channelRepository).should().findById(channelId);
            then(messageRepository).should().save(any(Message.class));
            then(binaryContentRepository).should(never()).save(any(BinaryContent.class));
        }

        @Test
        @DisplayName("첨부파일과 함께 메시지 생성 성공")
        void create_WithAttachments_Success() {
            // Given
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
            
            // BinaryContent save 시 ID를 설정해주는 모킹
            given(binaryContentRepository.save(any(BinaryContent.class))).willAnswer(invocation -> {
                BinaryContent savedContent = invocation.getArgument(0);
                ReflectionTestUtils.setField(savedContent, "id", attachmentId);
                return savedContent;
            });
            
            given(messageRepository.save(any(Message.class))).willReturn(message);
            given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

            // When
            MessageDto result = messageService.create(messageCreateRequest,
                    Collections.singletonList(attachmentRequest));

            // Then
            assertThat(result).isNotNull();
            assertThat(result.attachments()).hasSize(1);
            assertThat(result.attachments().get(0).fileName()).isEqualTo("test-file.jpg");

            then(binaryContentRepository).should().save(any(BinaryContent.class));
            then(binaryContentStorage).should().put(eq(attachmentId), eq(attachmentRequest.bytes()));
        }

        @Test
        @DisplayName("내용과 첨부파일 모두 없으면 메시지 생성 실패")
        void create_WithoutContentAndAttachments_ThrowsValidationException() {
            // Given
            MessageCreateRequest emptyRequest = new MessageCreateRequest(null, channelId, userId);

            // When & Then
            assertThatThrownBy(() -> messageService.create(emptyRequest, List.of()))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("메시지 내용 또는 첨부파일 중 하나는 필수입니다.");

            then(userRepository).should(never()).findById(any());
            then(channelRepository).should(never()).findById(any());
        }

        @Test
        @DisplayName("존재하지 않는 사용자로 메시지 생성 실패")
        void create_UserNotFound_ThrowsUserNotFoundException() {
            // Given
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> messageService.create(messageCreateRequest, Arrays.asList()))
                    .isInstanceOf(UserNotFoundException.class);

            then(userRepository).should().findById(userId);
            then(channelRepository).should(never()).findById(any());
        }

        @Test
        @DisplayName("존재하지 않는 채널로 메시지 생성 실패")
        void create_ChannelNotFound_ThrowsChannelNotFoundException() {
            // Given
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(channelRepository.findById(channelId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> messageService.create(messageCreateRequest, List.of()))
                    .isInstanceOf(ChannelNotFoundException.class);

            then(channelRepository).should().findById(channelId);
            then(messageRepository).should(never()).save(any());
        }
    }

    @Nested
    @DisplayName("채널별 메시지 조회 테스트")
    class GetAllByChannelIdTests {

        @Test
        @DisplayName("채널의 메시지 목록 조회 성공")
        void getAllByChannelId_WithMessages_Success() {
            // Given
            Pageable pageable = PageRequest.of(0, 20);
            Slice<Message> messageSlice = new SliceImpl<>(Collections.singletonList(message), pageable, true);
            PageResponse<MessageDto> pageResponse = new PageResponse<>(
                    Collections.singletonList(messageDto), // content
                    now,                                   // nextCursor
                    20,                                    // size
                    true,                                  // hasNext
                    null                                   // totalElements (Slice에서는 null)
            );

            given(messageRepository.findAllByChannelIdWithAuthor(eq(channelId), any(Instant.class), eq(pageable)))
                    .willReturn(messageSlice);
            given(messageMapper.toDto(message)).willReturn(messageDto);
            given(pageResponseMapper.fromSlice(any(Slice.class), eq(now))).willReturn(pageResponse);

            // When
            PageResponse<MessageDto> result = messageService.getAllByChannelId(channelId, now, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).id()).isEqualTo(messageId);
            assertThat(result.hasNext()).isTrue();
            assertThat(result.nextCursor()).isEqualTo(now);
            assertThat(result.size()).isEqualTo(20);
            assertThat(result.totalElements()).isNull();

            then(messageRepository).should().findAllByChannelIdWithAuthor(eq(channelId), eq(now), eq(pageable));
            then(messageMapper).should().toDto(message);
        }

        @Test
        @DisplayName("빈 채널의 메시지 조회시 빈 결과 반환")
        void getAllByChannelId_EmptyChannel_ReturnsEmptyResult() {
            // Given
            Pageable pageable = PageRequest.of(0, 20);
            Slice<Message> emptySlice = new SliceImpl<>(List.of(), pageable, false);
            PageResponse<MessageDto> emptyPageResponse = new PageResponse<>(
                    List.of(),     // content (빈 리스트)
                    null,          // nextCursor (null)
                    20,            // size
                    false,         // hasNext
                    null           // totalElements
            );

            given(messageRepository.findAllByChannelIdWithAuthor(eq(channelId), any(Instant.class), eq(pageable)))
                    .willReturn(emptySlice);
            given(pageResponseMapper.fromSlice(any(Slice.class), eq(null))).willReturn(emptyPageResponse);

            // When
            PageResponse<MessageDto> result = messageService.getAllByChannelId(channelId, now, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.content()).isEmpty();
            assertThat(result.hasNext()).isFalse();
            assertThat(result.nextCursor()).isNull();
            assertThat(result.size()).isEqualTo(20);

            then(messageMapper).should(never()).toDto(any(Message.class));
        }
    }

    @Nested
    @DisplayName("메시지 단건 조회 테스트")
    class FindMessageTests {

        @Test
        @DisplayName("메시지 ID로 조회 성공")
        void find_ExistingMessage_Success() {
            // Given
            given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
            given(messageMapper.toDto(message)).willReturn(messageDto);

            // When
            MessageDto result = messageService.find(messageId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(messageId);
            assertThat(result.content()).isEqualTo("안녕하세요! 테스트 메시지입니다.");

            then(messageRepository).should().findById(messageId);
            then(messageMapper).should().toDto(message);
        }

        @Test
        @DisplayName("존재하지 않는 메시지 조회시 실패")
        void find_NonExistingMessage_ThrowsMessageNotFoundException() {
            // Given
            given(messageRepository.findById(messageId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> messageService.find(messageId))
                    .isInstanceOf(MessageNotFoundException.class);

            then(messageRepository).should().findById(messageId);
            then(messageMapper).should(never()).toDto(any(Message.class));
        }
    }

    @Nested
    @DisplayName("메시지 수정 테스트")
    class UpdateMessageTests {

        @Test
        @DisplayName("메시지 내용 수정 성공")
        void update_ExistingMessage_Success() {
            // Given
            given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
            given(messageMapper.toDto(message)).willReturn(messageDto);

            // When
            MessageDto result = messageService.update(messageId, messageUpdateRequest);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(messageId);

            then(messageRepository).should().findById(messageId);
            then(messageMapper).should().toDto(message);
        }

        @Test
        @DisplayName("존재하지 않는 메시지 수정시 실패")
        void update_NonExistingMessage_ThrowsMessageNotFoundException() {
            // Given
            given(messageRepository.findById(messageId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> messageService.update(messageId, messageUpdateRequest))
                    .isInstanceOf(MessageNotFoundException.class);

            then(messageRepository).should().findById(messageId);
            then(messageMapper).should(never()).toDto(any(Message.class));
        }
    }

    @Nested
    @DisplayName("메시지 삭제 테스트")
    class DeleteMessageTests {

        @Test
        @DisplayName("메시지 삭제 성공")
        void delete_ExistingMessage_Success() {
            // Given
            given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
            willDoNothing().given(messageRepository).deleteById(messageId);

            // When
            messageService.delete(messageId);

            // Then
            then(messageRepository).should().findById(messageId);
            then(messageRepository).should().deleteById(messageId);
        }

        @Test
        @DisplayName("존재하지 않는 메시지 삭제시 실패")
        void delete_NonExistingMessage_ThrowsMessageNotFoundException() {
            // Given
            given(messageRepository.findById(messageId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> messageService.delete(messageId))
                    .isInstanceOf(MessageNotFoundException.class);

            then(messageRepository).should().findById(messageId);
            then(messageRepository).should(never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("메시지 검증 테스트")
    class MessageValidationTests {

        @Test
        @DisplayName("빈 내용과 첨부파일로 메시지 생성 실패")
        void create_EmptyContentWithoutAttachments_ThrowsValidationException() {
            // Given
            MessageCreateRequest emptyContentRequest = new MessageCreateRequest("", channelId, userId);

            // When & Then
            assertThatThrownBy(() -> messageService.create(emptyContentRequest, Arrays.asList()))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("메시지 내용 또는 첨부파일 중 하나는 필수입니다.");
        }

        @Test
        @DisplayName("공백만 있는 내용과 첨부파일 없이 메시지 생성 실패")
        void create_WhitespaceContentWithoutAttachments_ThrowsValidationException() {
            // Given
            MessageCreateRequest whitespaceRequest = new MessageCreateRequest("   ", channelId, userId);

            // When & Then
            assertThatThrownBy(() -> messageService.create(whitespaceRequest, Arrays.asList()))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("메시지 내용 또는 첨부파일 중 하나는 필수입니다.");
        }

        @Test
        @DisplayName("내용 없이 첨부파일만으로 메시지 생성 성공")
        void create_NoContentWithAttachments_Success() {
            // Given
            MessageCreateRequest noContentRequest = new MessageCreateRequest(null, channelId, userId);
            
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
            
            // BinaryContent save 시 ID를 설정해주는 모킹
            given(binaryContentRepository.save(any(BinaryContent.class))).willAnswer(invocation -> {
                BinaryContent savedContent = invocation.getArgument(0);
                ReflectionTestUtils.setField(savedContent, "id", attachmentId);
                return savedContent;
            });
            
            given(messageRepository.save(any(Message.class))).willReturn(message);
            given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

            // When
            MessageDto result = messageService.create(noContentRequest, Arrays.asList(attachmentRequest));

            // Then
            assertThat(result).isNotNull();
            
            then(binaryContentRepository).should().save(any(BinaryContent.class));
            then(messageRepository).should().save(any(Message.class));
            then(binaryContentStorage).should().put(eq(attachmentId), eq(attachmentRequest.bytes()));
        }
    }
}
