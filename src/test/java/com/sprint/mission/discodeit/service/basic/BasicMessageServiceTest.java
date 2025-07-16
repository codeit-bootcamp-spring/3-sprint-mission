package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.NotFoundChannelException;
import com.sprint.mission.discodeit.exception.message.NotFoundMessageException;
import com.sprint.mission.discodeit.exception.user.NotFoundUserException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.struct.BinaryContentStructMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Message 서비스 단위 테스트")
class BasicMessageServiceTest {

    @InjectMocks
    private BasicMessageService messageService;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @Mock
    private BinaryContentStructMapper binaryContentMapper;

    @Test
    @DisplayName("정상적인 Message 생성 시 올바른 비즈니스 로직이 수행되어야 한다.")
    void givenValidRequest_whenCreateMessage_thenCreateSuccessfully() {

        // given
        String content = "Hello";
        UUID messageId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        byte[] imageBytes = new byte[]{1, 2, 3};

        BinaryContentDto binaryContentDto = new BinaryContentDto("attachment.png", 3L,
                "image/png", imageBytes);

        BinaryContent binaryContent = new BinaryContent("attachment.png", 3L, "image/png");

        UUID attachmentId = UUID.randomUUID();
        ReflectionTestUtils.setField(binaryContent, "id", attachmentId);

        MessageRequestDto request = new MessageRequestDto(content, channelId, authorId);

        User user = User.builder()
                .username("test")
                .email("test@test.com")
                .password("pwd1234")
                .build();

        Channel channel = Channel.builder()
                .name("public")
                .description("test channel")
                .type(ChannelType.PUBLIC)
                .build();

        Message message = Message.builder()
                .author(user)
                .channel(channel)
                .content(content)
                .attachments(List.of(binaryContent))
                .build();

        ReflectionTestUtils.setField(user, "id", authorId);
        ReflectionTestUtils.setField(channel, "id", channelId);
        ReflectionTestUtils.setField(message, "id", messageId);

        BinaryContentResponseDto attachment = new BinaryContentResponseDto(attachmentId, "attachment.png", 3L,
                "image/png");

        MessageResponseDto messageResponseDto = new MessageResponseDto(messageId, Instant.now(), Instant.now(),
                content, channelId, null, List.of(attachment));

        given(userRepository.findById(authorId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(binaryContentMapper.toEntity(binaryContentDto)).willReturn(binaryContent);
        given(messageRepository.save(any(Message.class))).willReturn(message);
        given(messageMapper.toDto(any(Message.class))).willReturn(messageResponseDto);

        // when
        MessageResponseDto result = messageService.create(request, List.of(binaryContentDto));

        // then
        assertNotNull(result);
        assertEquals(content, result.content());
        assertEquals(channelId, result.channelId());
        assertEquals(List.of(attachment), result.attachments());

        verify(userRepository).findById(authorId);
        verify(channelRepository).findById(channelId);
        verify(binaryContentRepository).saveAll(List.of(binaryContent));
        verify(binaryContentStorage).put(attachmentId, imageBytes);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 메시지를 전송하면 NotFoundUserException이 발생해야 한다.")
    void givenInvalidUserId_whenCreateMessage_thenThrowNotFoundUserException() {

        // given
        UUID notExistUserId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        MessageRequestDto request = new MessageRequestDto("Hello", channelId, notExistUserId);

        given(userRepository.findById(notExistUserId)).willReturn(Optional.empty());

        // when
        assertThrows(NotFoundUserException.class, () -> messageService.create(request, null));

        // then
        verify(messageRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 채널 ID로 메시지를 전송하면 NotFoundChannelException이 발생해야 한다.")
    void givenInvalidChannelId_whenCreateMessage_thenThrowNotFoundChannelException() {

        // given
        UUID userId = UUID.randomUUID();
        UUID notExistChannelId = UUID.randomUUID();

        MessageRequestDto request = new MessageRequestDto("Hello", notExistChannelId, userId);

        User user = User.builder()
                .username("test")
                .email("test@test.com")
                .password("pwd1234")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(channelRepository.findById(notExistChannelId)).willReturn(Optional.empty());

        // when
        assertThrows(NotFoundChannelException.class, () -> messageService.create(request, null));

        // then
        verify(messageRepository, never()).save(any());
    }

    @Test
    @DisplayName("메시지 ID로 메시지를 조회하면 정상적으로 조회되어야 한다.")
    void givenValidMessageId_whenFindMessage_thenReturnDto() {

        // given
        String content = "Hello";
        UUID messageId = UUID.randomUUID();

        User user = User.builder()
                .username("test")
                .email("test@test.com")
                .password("pwd1234")
                .build();

        Channel channel = Channel.builder()
                .name("public")
                .description("test channel")
                .type(ChannelType.PUBLIC)
                .build();

        Message message = Message.builder()
                .content(content)
                .author(user)
                .channel(channel)
                .attachments(List.of())
                .build();

        ReflectionTestUtils.setField(message, "id", messageId);
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(channel, "id", UUID.randomUUID());

        MessageResponseDto expectedDto = new MessageResponseDto(
                messageId, Instant.now(), Instant.now(),
                content, channel.getId(), null, List.of()
        );

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
        given(messageMapper.toDto(message)).willReturn(expectedDto);

        // when
        MessageResponseDto result = messageService.findById(messageId);

        // then
        assertNotNull(result);
        assertEquals(content, result.content());
        assertEquals(channel.getId(), result.channelId());

        verify(messageRepository).findById(messageId);
        verify(messageMapper).toDto(message);
    }

    @Test
    @DisplayName("등록되지 않은 ID로 메시지를 조회하면 NotFoundMessageException이 발생해야 한다.")
    void givenInvalidMessageId_whenFindMessage_thenThrowNotFoundMessageException() {

        // given
        UUID notExistId = UUID.randomUUID();

        given(messageRepository.findById(notExistId)).willReturn(Optional.empty());

        // when
        assertThrows(NotFoundMessageException.class, () -> messageService.findById(notExistId));

        // then
        verify(messageRepository).findById(notExistId);
        verifyNoInteractions(messageMapper);
    }

    @Test
    @DisplayName("채널 ID로 메시지를 페이지네이션 조회할 때 cursor가 null이면 최신 메시지를 반환해야 한다.")
    void givenNullCursor_whenFindMessagesByChannelId_thenReturnFirstPage() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID messageId1 = UUID.randomUUID();
        UUID messageId2 = UUID.randomUUID();

        int size = 2;
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Pageable extendedPageable = PageRequest.of(0, size + 1, Sort.by(Sort.Direction.DESC, "createdAt"));

        Channel channel = Channel.builder().name("channel").type(ChannelType.PUBLIC).build();
        ReflectionTestUtils.setField(channel, "id", channelId);

        Instant now = Instant.now();
        Instant t1 = now.minusSeconds(10);
        Instant t2 = now.minusSeconds(20);

        Message msg1 = Message.builder().content("Hello").channel(channel).build();
        Message msg2 = Message.builder().content("World").channel(channel).build();
        Message msg3 = Message.builder().content("NextPage").channel(channel).build();

        ReflectionTestUtils.setField(msg1, "createdAt", now);
        ReflectionTestUtils.setField(msg2, "createdAt", t1);
        ReflectionTestUtils.setField(msg3, "createdAt", t2);
        ReflectionTestUtils.setField(msg1, "id", messageId1);
        ReflectionTestUtils.setField(msg2, "id", messageId2);

        List<Message> allMessages = List.of(msg1, msg2, msg3); // size + 1개

        MessageResponseDto dto1 = new MessageResponseDto(messageId1, Instant.now(), Instant.now(), "Hello", channelId, null, List.of());
        MessageResponseDto dto2 = new MessageResponseDto(messageId2, Instant.now(), Instant.now(), "World", channelId, null, List.of());

        given(messageRepository.findPageByChannelId(channelId, extendedPageable)).willReturn(allMessages);
        given(messageMapper.toDto(msg1)).willReturn(dto1);
        given(messageMapper.toDto(msg2)).willReturn(dto2);

        // when
        PageResponse<MessageResponseDto> result = messageService.findAllByChannelId(channelId, null, pageable);

        // then
        assertEquals(size, result.content().size());
        assertTrue(result.hasNext());
        assertEquals(dto1, result.content().get(0));
        assertEquals(dto2, result.content().get(1));

        verify(messageRepository).findPageByChannelId(channelId, extendedPageable);
        verify(messageMapper).toDto(msg1);
        verify(messageMapper).toDto(msg2);
    }

    @Test
    @DisplayName("cursor가 주어지면 해당 시간 이전 메시지를 최신순으로 페이지네이션 조회해야 한다.")
    void givenCursor_whenFindMessagesByChannelId_thenReturnPreviousMessagesPage() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID messageId1 = UUID.randomUUID();
        UUID messageId2 = UUID.randomUUID();

        int size = 2;
        Instant cursor = Instant.now(); // 기준 시간
        Instant t1 = cursor.minusSeconds(10);
        Instant t2 = cursor.minusSeconds(20);
        Instant t3 = cursor.minusSeconds(30);

        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Pageable extendedPageable = PageRequest.of(0, size + 1, Sort.by(Sort.Direction.DESC, "createdAt"));

        Channel channel = Channel.builder().name("channel").type(ChannelType.PUBLIC).build();
        ReflectionTestUtils.setField(channel, "id", channelId);

        Message msg1 = Message.builder().content("Msg1").channel(channel).build();
        Message msg2 = Message.builder().content("Msg2").channel(channel).build();
        Message msg3 = Message.builder().content("Msg3").channel(channel).build();

        ReflectionTestUtils.setField(msg1, "createdAt", t1);
        ReflectionTestUtils.setField(msg2, "createdAt", t2);
        ReflectionTestUtils.setField(msg3, "createdAt", t3);
        ReflectionTestUtils.setField(msg1, "id", messageId1);
        ReflectionTestUtils.setField(msg2, "id", messageId2);

        List<Message> messages = List.of(msg1, msg2, msg3); // size + 1개

        MessageResponseDto dto1 = new MessageResponseDto(messageId1, t1, t1, "Msg1", channelId, null, List.of());
        MessageResponseDto dto2 = new MessageResponseDto(messageId2, t2, t2, "Msg2", channelId, null, List.of());

        given(messageRepository.findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(channelId, cursor, extendedPageable))
                .willReturn(messages);
        given(messageMapper.toDto(msg1)).willReturn(dto1);
        given(messageMapper.toDto(msg2)).willReturn(dto2);

        // when
        PageResponse<MessageResponseDto> result = messageService.findAllByChannelId(channelId, cursor, pageable);

        // then
        assertEquals(size, result.content().size());
        assertTrue(result.hasNext());
        assertEquals(dto1, result.content().get(0));
        assertEquals(dto2, result.content().get(1));
        assertEquals(t2, result.nextCursor()); // 마지막 메시지의 createdAt

        verify(messageRepository).findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(channelId, cursor, extendedPageable);
        verify(messageMapper).toDto(msg1);
        verify(messageMapper).toDto(msg2);
        verifyNoMoreInteractions(messageMapper); // msg3는 변환 안됨
    }

    @Test
    @DisplayName("등록된 메시지 ID로 메시지를 업데이트하면 올바른 비즈니스 로직이 수행되어야 한다.")
    void givenValidMessageIdAndContent_whenUpdateMessage_thenUpdateSuccessfully() {

        // given
        UUID messageId = UUID.randomUUID();

        Message message = Message.builder()
                .content("Hello")
                .build();

        ReflectionTestUtils.setField(message, "id", messageId);

        String newContent = "Hi";

        Message updatedMessage = Message.builder()
                .content(newContent)
                .build();

        MessageResponseDto expectedMessage = new MessageResponseDto(messageId, Instant.now(), Instant.now(), newContent,
                null, null, List.of());

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
        given(messageMapper.toDto(any(Message.class))).willReturn(expectedMessage);
        given(messageRepository.save(any(Message.class))).willReturn(updatedMessage);

        // when
        MessageResponseDto result = messageService.updateContent(messageId, newContent);

        // then
        assertNotNull(result);
        assertEquals(expectedMessage, result);

        verify(messageRepository).findById(messageId);
    }

    @Test
    @DisplayName("등록되지 않은 메시지 ID로 메시지를 업데이트하면 NotFoundMessageException이 발생해야 한다.")
    void givenInvalidMessageId_whenUpdateMessage_thenThrowNotFoundMessageException() {

        // given
        UUID notExistId = UUID.randomUUID();

        given(messageRepository.findById(notExistId)).willReturn(Optional.empty());

        // when
        assertThrows(NotFoundMessageException.class, () -> messageService.updateContent(notExistId, "Hello"));

        // then
        verify(messageRepository).findById(notExistId);
    }

    @Test
    @DisplayName("등록된 메시지 ID로 삭제하면 첨부파일도 삭제되어야 한다.")
    void givenValidMessageId_whenDeleteMessage_thenDeleteWithAttachments() {

        // given
        UUID messageId = UUID.randomUUID();
        BinaryContent attachment = new BinaryContent("attachment.jpg", 3L,
                "image/jpeg");

        ReflectionTestUtils.setField(attachment, "id", UUID.randomUUID());

        Message message = Message.builder()
                .attachments(List.of(attachment))
                .build();

        ReflectionTestUtils.setField(message, "id", messageId);

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

        // when
        messageService.deleteById(messageId);

        // then
        verify(messageRepository).findById(messageId);
        verify(binaryContentRepository).deleteById(message.getAttachments().get(0).getId());
        verify(messageRepository).deleteById(messageId);
    }

    @Test
    @DisplayName("등록되지 않은 메시지 ID로 삭제하면 NotFoundMessageException이 발생해야 한다.")
    void givenInvalidMessageId_whenDeleteMessage_thenThrowNotFoundMessageException() {

        // given
        UUID notExistId = UUID.randomUUID();
        given(messageRepository.findById(notExistId)).willReturn(Optional.empty());

        // when
        assertThrows(NotFoundMessageException.class, () -> messageService.deleteById(notExistId));

        // then
        verify(messageRepository).findById(notExistId);
        verifyNoMoreInteractions(binaryContentRepository);
    }
}