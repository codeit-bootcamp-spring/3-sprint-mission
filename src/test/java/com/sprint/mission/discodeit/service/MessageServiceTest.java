package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.UserDto;
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
import java.util.ArrayList;
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
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasicMessageService 단위 테스트")
public class MessageServiceTest {

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
    private PageResponseMapper pageResponseMapper;

    @InjectMocks
    private BasicMessageService messageService;

    private UUID userId;
    private UUID channelId;
    private UUID messageId;
    private User user;
    private Channel channel;
    private Message message;
    private MessageDto messageDto;
    private String content;
    private BinaryContent attachment;
    private BinaryContentDto binaryContentDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        channelId = UUID.randomUUID();
        messageId = UUID.randomUUID();
        content = "hello";

        user = new User("testuser", "test@abc.com", "1234", null);
        ReflectionTestUtils.setField(user, "id", userId);
        channel = new Channel(ChannelType.PUBLIC, "공지사항", "업데이트 내용");
        ReflectionTestUtils.setField(channel, "id", channelId);
        attachment = new BinaryContent("이미지", 1024L, "image/png");
        ReflectionTestUtils.setField(attachment, "id", UUID.randomUUID());
        message = new Message(content, channel, user, List.of(attachment));

        binaryContentDto = new BinaryContentDto(attachment.getId(), "이미지", 1024L, "image/png");
        messageDto = new MessageDto(
            messageId,
            Instant.now(),
            Instant.now(),
            content,
            channelId,
            new UserDto(userId, "testuser", "test@abc.com", null, true),
            List.of(binaryContentDto)
        );
    }

    @Test
    @DisplayName("메시지를 생성할 수 있다.")
    void createMessage_success() {
        //given
        MessageCreateRequest request = new MessageCreateRequest(content, channelId, userId);
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(messageRepository.save(any())).willReturn(message);
        given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

        //when
        MessageDto result = messageService.createMessage(request, null);

        //then
        assertThat(result).isEqualTo(messageDto);
    }

    @Test
    @DisplayName("첨부파일이 포함된 메시지를 생성할 수 있다.")
    void createMessage_success_withAttachments() {
        //given
        byte[] testBytes = "file".getBytes();
        BinaryContentCreateRequest fileReq = new BinaryContentCreateRequest("이미지", 1024L,
            "image/png", testBytes);
        List<BinaryContentCreateRequest> attachments = List.of(fileReq);
        MessageCreateRequest request = new MessageCreateRequest(content, channelId, userId);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(attachment);
        given(messageRepository.save(any(Message.class))).willReturn(message);
        given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

        //when
        MessageDto result = messageService.createMessage(request, attachments);

        //then
        assertThat(result).isEqualTo(messageDto);
        then(messageRepository).should().save(any(Message.class));
        then(binaryContentStorage).should().put(eq(attachment.getId()), eq(testBytes));
    }

    @Test
    @DisplayName("채널이 없으면 메시지를 생성할 수 없다.")
    void createMessage_fail_ifChannelNotFound() {
        //given
        MessageCreateRequest request = new MessageCreateRequest(content, channelId, userId);
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> messageService.createMessage(request, null))
            .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("유저가 없으면 메시지를 생성할 수 없다.")
    void createMessage_fail_ifUserNotFound() {
        //given
        MessageCreateRequest request = new MessageCreateRequest(content, channelId, userId);
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> messageService.createMessage(request, null))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("메시지를 수정할 수 있다.")
    void updateMessage_success() {
        //given
        MessageUpdateRequest request = new MessageUpdateRequest("new Message");
        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
        given(messageRepository.save(any())).willReturn(message);
        given(messageMapper.toDto(eq(message))).willReturn(messageDto);

        //when
        MessageDto result = messageService.updateMessage(messageId, request);

        //then
        assertThat(result).isEqualTo(messageDto);
    }

    @Test
    @DisplayName("메시지가 존재하지 않으면 수정할 수 없다.")
    void updateMessage_fail_ifNotFound() {
        //given
        MessageUpdateRequest request = new MessageUpdateRequest("new Message");
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> messageService.updateMessage(messageId, request))
            .isInstanceOf(MessageNotFoundException.class);
    }

    @Test
    @DisplayName("메시지를 삭제할 수 있다.")
    void deleteMessage_success() {
        //given
        message = new Message(content, channel, user, new ArrayList<>());
        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

        //when
        messageService.deleteMessage(messageId, user.getId());

        //then
        then(messageRepository).should().deleteById(messageId);
    }

    @Test
    @DisplayName("존재하지 않는 메시지는 삭제할 수 없다.")
    void deleteMessage_fail_ifMessageNotFound() {
        //given
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> messageService.deleteMessage(messageId, userId))
            .isInstanceOf(MessageNotFoundException.class);
    }

    @Test
    @DisplayName("채널별 메시지를 커서 페이지네이션 조회할 수 있다.")
    void findAllByChannelId_success() {
        //given
        int pageSize = 2;
        Instant createdAt = Instant.now();
        Pageable pageable = PageRequest.of(0, pageSize);

        Message message1 = new Message(content, channel, user, List.of(attachment));
        Message message2 = new Message(content, channel, user, List.of(attachment));

        ReflectionTestUtils.setField(message1, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(message2, "id", UUID.randomUUID());

        Instant message1CreatedAt = Instant.now().minusSeconds(30);
        Instant message2CreatedAt = Instant.now().minusSeconds(20);

        ReflectionTestUtils.setField(message1, "createdAt", message1CreatedAt);
        ReflectionTestUtils.setField(message2, "createdAt", message2CreatedAt);

        MessageDto messageDto1 = new MessageDto(
            message1.getId(),
            message1CreatedAt,
            message1CreatedAt,
            content,
            channelId,
            new UserDto(userId, "testuser", "test@abc.com", null, true),
            List.of(binaryContentDto)
        );

        MessageDto messageDto2 = new MessageDto(
            message2.getId(),
            message2CreatedAt,
            message2CreatedAt,
            content,
            channelId,
            new UserDto(userId, "testuser", "test@abc.com", null, true), null
        );

        List<Message> PageMessages = List.of(message1, message2);
        List<MessageDto> PageDtos = List.of(messageDto1, messageDto2);

        SliceImpl<Message> firstPageSlice = new SliceImpl<>(PageMessages, pageable, true);
        PageResponse<MessageDto> firstPageResponse = new PageResponse<>(
            PageDtos,
            message2CreatedAt,
            pageSize,
            true,
            null
        );

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(messageRepository.findAllByChannelIdWithAuthor(eq(channelId), eq(createdAt),
            eq(pageable)))
            .willReturn(firstPageSlice);
        given(messageMapper.toDto(eq(message1))).willReturn(messageDto1);
        given(messageMapper.toDto(eq(message2))).willReturn(messageDto2);
        given(pageResponseMapper.<MessageDto>fromSlice(any(), eq(message2CreatedAt)))
            .willReturn(firstPageResponse);

        //when
        PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, createdAt,
            pageable);

        //then
        assertThat(result).isEqualTo(firstPageResponse);
        assertThat(result.content()).hasSize(pageSize);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.nextCursor()).isEqualTo(message2CreatedAt);
    }

}