package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
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

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

/**
 * MessageService의 주요 기능(메시지 생성, 수정, 삭제, 조회 등)에 대한 단위 테스트 클래스입니다.
 * <p>
 * - 메시지 생성, 수정, 삭제, 예외 상황 등을 검증합니다.
 * - Mockito를 활용한 Mock 객체 기반 테스트입니다.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MessageService 단위 테스트")
class MessageServiceTest {

    @InjectMocks
    private BasicMessageService basicMessageService;

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

    private UUID messageId;
    private UUID channelId;
    private UUID userId;
    private UUID authorId;
    private Message mockMessage;
    private Channel mockChannel;
    private User mockUser;
    private MessageCreateRequest validCreateRequest;
    private MessageUpdateRequest validUpdateRequest;
    private BinaryContentCreateRequest validAttachmentRequest;

    @BeforeEach
    void setUp() {
        messageId = UUID.randomUUID();
        channelId = UUID.randomUUID();
        userId = UUID.randomUUID();
        authorId = UUID.randomUUID();

        mockChannel = new Channel(ChannelType.PUBLIC, "테스트 채널", "테스트 채널입니다");
        mockChannel.setId(channelId);

        mockUser = new User("테스트유저", "test@example.com", "password", null);
        mockUser.setId(authorId);

        mockMessage = new Message("테스트 메시지", mockChannel, mockUser, List.of());
        mockMessage.setId(messageId);

        validCreateRequest = new MessageCreateRequest("테스트 메시지", channelId, authorId);
        validUpdateRequest = new MessageUpdateRequest("수정된 메시지");
        validAttachmentRequest = new BinaryContentCreateRequest("test.jpg", "image/jpeg", new byte[]{1, 2, 3});
    }

    @Nested
    @DisplayName("메시지 생성 테스트")
    class CreateMessageTests {

        /**
         * [성공] 첨부파일 없이 메시지를 정상적으로 생성하는지 검증합니다.
         */
        @Test
        @DisplayName("[성공] 첨부파일 없이 메시지 생성")
        void create_ShouldCreateMessageSuccessfully_WhenNoAttachments() {
            // Given
            given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));
            given(userRepository.findById(authorId)).willReturn(Optional.of(mockUser));
            given(messageRepository.save(any(Message.class))).willReturn(mockMessage);
            given(messageMapper.toDto(any(Message.class))).willReturn(createMockMessageDto());

            // When
            MessageDto result = basicMessageService.create(validCreateRequest, List.of());

            // Then
            assertThat(result).isNotNull();
            then(channelRepository).should().findById(channelId);
            then(userRepository).should().findById(authorId);
            then(messageRepository).should().save(any(Message.class));
            then(binaryContentRepository).shouldHaveNoInteractions();
            then(binaryContentStorage).shouldHaveNoInteractions();
        }

        /**
         * [성공] 첨부파일이 있을 때 메시지를 정상적으로 생성하는지 검증합니다.
         */
        @Test
        @DisplayName("[성공] 첨부파일과 함께 메시지 생성")
        void create_ShouldCreateMessageSuccessfully_WhenWithAttachments() {
            // Given
            BinaryContent mockAttachment = new BinaryContent("test.jpg", 3L, "image/jpeg");
            mockAttachment.setId(UUID.randomUUID());
            Message messageWithAttachment = new Message("테스트 메시지", mockChannel, mockUser, List.of(mockAttachment));
            messageWithAttachment.setId(messageId);

            given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));
            given(userRepository.findById(authorId)).willReturn(Optional.of(mockUser));
            given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(mockAttachment);
            given(messageRepository.save(any(Message.class))).willReturn(messageWithAttachment);
            given(messageMapper.toDto(any(Message.class))).willReturn(createMockMessageDto());

            // When
            MessageDto result = basicMessageService.create(validCreateRequest, List.of(validAttachmentRequest));

            // Then
            assertThat(result).isNotNull();
            then(binaryContentRepository).should().save(any(BinaryContent.class));
            then(binaryContentStorage).should().put(eq(mockAttachment.getId()), eq(new byte[]{1, 2, 3}));
            then(messageRepository).should().save(any(Message.class));
        }

        /**
         * [실패] 채널이 존재하지 않을 때 예외가 발생하는지 검증합니다.
         */
        @Test
        @DisplayName("[실패] 메시지 생성 - 채널 없음")
        void create_ShouldThrowException_WhenChannelNotFound() {
            // Given
            given(channelRepository.findById(channelId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> basicMessageService.create(validCreateRequest, List.of()))
                    .isInstanceOf(MessageNotFoundException.class)
                    .hasMessage("채널을 찾을 수 없습니다.");

            then(channelRepository).should().findById(channelId);
            then(userRepository).shouldHaveNoInteractions();
            then(messageRepository).shouldHaveNoInteractions();
        }

        /**
         * [실패] 사용자가 존재하지 않을 때 예외가 발생하는지 검증합니다.
         */
        @Test
        @DisplayName("[실패] 메시지 생성 - 사용자 없음")
        void create_ShouldThrowException_WhenUserNotFound() {
            // Given
            given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));
            given(userRepository.findById(authorId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> basicMessageService.create(validCreateRequest, List.of()))
                    .isInstanceOf(MessageNotFoundException.class)
                    .hasMessage("사용자를 찾을 수 없습니다.");

            then(channelRepository).should().findById(channelId);
            then(userRepository).should().findById(authorId);
            then(messageRepository).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("메시지 수정 테스트")
    class UpdateMessageTests {

        @Test
        @DisplayName("메시지 수정 성공")
        void update_ShouldUpdateMessageSuccessfully() {
            // Given
            given(messageRepository.findById(messageId)).willReturn(Optional.of(mockMessage));
            given(messageMapper.toDto(any(Message.class))).willReturn(createMockMessageDto());

            // When
            MessageDto result = basicMessageService.update(messageId, validUpdateRequest);

            // Then
            assertThat(result).isNotNull();
            then(messageRepository).should().findById(messageId);
            then(messageRepository).should().save(mockMessage);
            assertThat(mockMessage.getContent()).isEqualTo("수정된 메시지");
        }

        @Test
        @DisplayName("메시지 수정 실패 - 메시지가 존재하지 않음")
        void update_ShouldThrowException_WhenMessageNotFound() {
            // Given
            given(messageRepository.findById(messageId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> basicMessageService.update(messageId, validUpdateRequest))
                    .isInstanceOf(MessageNotFoundException.class)
                    .hasMessage("메시지를 찾을 수 없습니다.");

            then(messageRepository).should().findById(messageId);
            then(messageRepository).should(never()).save(any());
        }
    }

    @Nested
    @DisplayName("메시지 삭제 테스트")
    class DeleteMessageTests {

        @Test
        @DisplayName("메시지 삭제 성공 - 첨부파일 없음")
        void delete_ShouldDeleteMessageSuccessfully_WhenNoAttachments() {
            // Given
            given(messageRepository.findById(messageId)).willReturn(Optional.of(mockMessage));

            // When
            basicMessageService.delete(messageId);

            // Then
            then(messageRepository).should().findById(messageId);
            then(messageRepository).should().deleteById(messageId);
            then(binaryContentRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("메시지 삭제 성공 - 첨부파일 있음")
        void delete_ShouldDeleteMessageAndAttachments_WhenWithAttachments() {
            // Given
            BinaryContent mockAttachment = new BinaryContent("test.jpg", 3L, "image/jpeg");
            mockAttachment.setId(UUID.randomUUID());
            Message messageWithAttachment = new Message("테스트 메시지", mockChannel, mockUser, List.of(mockAttachment));
            messageWithAttachment.setId(messageId);

            given(messageRepository.findById(messageId)).willReturn(Optional.of(messageWithAttachment));

            // When
            basicMessageService.delete(messageId);

            // Then
            then(messageRepository).should().findById(messageId);
            then(binaryContentRepository).should().deleteById(mockAttachment.getId());
            then(messageRepository).should().deleteById(messageId);
        }

        @Test
        @DisplayName("메시지 삭제 실패 - 메시지가 존재하지 않음")
        void delete_ShouldThrowException_WhenMessageNotFound() {
            // Given
            given(messageRepository.findById(messageId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> basicMessageService.delete(messageId))
                    .isInstanceOf(MessageNotFoundException.class)
                    .hasMessage("메시지를 찾을 수 없습니다.");

            then(messageRepository).should().findById(messageId);
            then(messageRepository).should(never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("채널별 메시지 조회 테스트")
    class FindByChannelIdTests {

        @Test
        @DisplayName("채널별 메시지 조회 성공")
        void findByChannelId_ShouldReturnMessagesSuccessfully() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Instant cursor = Instant.now();
            List<Message> mockMessages = List.of(mockMessage);
            Slice<Message> mockSlice = new SliceImpl<>(mockMessages, pageable, true);
            List<MessageDto> mockMessageDtos = List.of(createMockMessageDto());
            PageResponse<MessageDto> mockPageResponse = new PageResponse<>(mockMessageDtos, cursor, 10, true, 1L);

            given(messageRepository.findAllByChannelIdWithAuthor(eq(channelId), eq(cursor), eq(pageable)))
                    .willReturn(mockSlice);
            given(messageMapper.toDto(any(Message.class))).willReturn(createMockMessageDto());
            given(pageResponseMapper.fromSlice(any(Slice.class), any(Instant.class))).willReturn(mockPageResponse);

            // When
            PageResponse<MessageDto> result = basicMessageService.findAllByChannelIdWithAuthor(channelId, cursor, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            assertThat(result.hasNext()).isTrue();
            then(messageRepository).should().findAllByChannelIdWithAuthor(channelId, cursor, pageable);
        }

        @Test
        @DisplayName("채널별 메시지 조회 성공 - 빈 결과")
        void findByChannelId_ShouldReturnEmptyResult_WhenNoMessages() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Instant cursor = Instant.now();
            Slice<Message> mockSlice = new SliceImpl<>(List.of(), pageable, false);
            PageResponse<MessageDto> mockPageResponse = new PageResponse<>(List.of(), null, 10, false, 0L);

            given(messageRepository.findAllByChannelIdWithAuthor(eq(channelId), eq(cursor), eq(pageable)))
                    .willReturn(mockSlice);
            given(pageResponseMapper.fromSlice(any(Slice.class), eq(null))).willReturn(mockPageResponse);

            // When
            PageResponse<MessageDto> result = basicMessageService.findAllByChannelIdWithAuthor(channelId, cursor, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.content()).isEmpty();
            assertThat(result.hasNext()).isFalse();
            assertThat(result.nextCursor()).isNull();
        }
    }

    private MessageDto createMockMessageDto() {
        return new MessageDto(
                messageId,
                Instant.now(),
                Instant.now(),
                "테스트 메시지",
                channelId,
                new UserDto(authorId, "테스트유저", "test@example.com", null, true),
                List.of()
        );
    }
} 