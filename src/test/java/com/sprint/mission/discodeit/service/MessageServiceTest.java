package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageDto;
import com.sprint.mission.discodeit.dto.Message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.Channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.Message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.User.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
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
public class MessageServiceTest {

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

    @InjectMocks
    private BasicMessageService messageService;

    private Instant fixedNow = Instant.parse("2025-01-01T01:00:00Z");

    private User createUser(String userName, String email, String password) {
        User user = new User(userName, email, password, null, null);
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
        return user;
    }

    private Channel createChannel(ChannelType channelType, String name, String description) {
        Channel channel = new Channel(channelType, name, description);
        ReflectionTestUtils.setField(channel, "id", UUID.randomUUID());
        return channel;
    }

    private Message createMessage(String content, Channel ch, User user) {
        Message message = new Message(content, ch, user, null);
        ReflectionTestUtils.setField(message, "id", UUID.randomUUID());
        return message;
    }

    private BinaryContent createAttachment(String name, Long size, String type) {
        BinaryContent attachment = new BinaryContent(name, size, type);
        ReflectionTestUtils.setField(attachment, "id", UUID.randomUUID());
        return attachment;
    }

    private UserDto createUserDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), null,
                true);
    }

    private MessageDto createMessageDto(Message message, Channel ch, User user, Instant time) {
        UserDto userDto = createUserDto(user);
        return new MessageDto(
                message.getId(), time, time, message.getContent(),
                ch.getId(), userDto, new ArrayList<>()
        );
    }

    private MessageDto createMessageDtoWithAttachments(Message message, Channel ch, User user,
            List<BinaryContentDto> attachments) {
        UserDto userDto = createUserDto(user);
        return new MessageDto(message.getId(), fixedNow, fixedNow,
                message.getContent(), ch.getId(), userDto, attachments);
    }

    private BinaryContentDto createBinaryContentDto(BinaryContent attachment) {
        return new BinaryContentDto(attachment.getId(), attachment.getFileName(),
                attachment.getSize(), attachment.getContentType());
    }

    @Test
    @DisplayName("유효한 생성 요청으로 첨부파일이 포함된 메시지를 생성할 수 있다.")
    void shouldCreateMessageWithAttachment_whenValidRequest() {

        // given
        User user = createUser("테스터", "tester@codeit.com", "test1234");
        Channel channel = createChannel(ChannelType.PUBLIC, "테스트 채널", "테스트 채널입니다.");
        byte[] testBytes = "테스트 이미지".getBytes(StandardCharsets.UTF_8);
        BinaryContent attachment = createAttachment("test.png", 1024L, "image/png");
        UUID userId = user.getId();
        UUID channelId = channel.getId();
        UUID attachmentId = attachment.getId();
        String content = "메시지 생성 테스트입니다.";
        BinaryContentDto attachmentDto = createBinaryContentDto(attachment);
        MessageCreateRequest createRequest = new MessageCreateRequest(content, userId, channelId);
        BinaryContentCreateRequest binaryRequest = new BinaryContentCreateRequest(
                attachment.getFileName(), attachment.getContentType(), testBytes);
        Message message = createMessage(content, channel, user);
        MessageDto expectedDto = createMessageDtoWithAttachments(message, channel, user,
                List.of(attachmentDto));

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(binaryContentRepository.saveAll(anyList())).willReturn(List.of(attachment));
        given(binaryContentStorage.put(any(), any())).willReturn(attachmentId);
        given(messageRepository.save(any(Message.class))).willReturn(message);
        given(messageMapper.toDto(any(Message.class))).willReturn(expectedDto);

        // when
        MessageDto result = messageService.create(createRequest, List.of(binaryRequest));

        // then
        assertThat(result.content()).isEqualTo(content);
        assertThat(result.channelId()).isEqualTo(channelId);
        assertThat(result.author().id()).isEqualTo(user.getId());
        assertThat(result.attachments()).hasSize(1);
        assertThat(result.attachments().get(0).id()).isEqualTo(attachmentId);
    }

    @Test
    @DisplayName("존재하지 않는 유저로 메시지 전송 시 예외가 발생한다.")
    void shouldThrowException_whenNonExistentUser() {

        // given
        Channel channel = createChannel(ChannelType.PUBLIC, "테스트 채널", "테스트 채널입니다.");
        UUID userId = UUID.randomUUID();
        UUID channelId = channel.getId();
        String content = "메시지 생성 테스트입니다.";
        MessageCreateRequest createRequest = new MessageCreateRequest(content, userId, channelId);

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> {
            messageService.create(createRequest, new ArrayList<>());
        }).isInstanceOf(UserNotFoundException.class);

        then(userRepository).should().findById(userId);
        then(channelRepository).shouldHaveNoInteractions();
        then(binaryContentStorage).shouldHaveNoInteractions();
        then(binaryContentRepository).shouldHaveNoInteractions();
        then(messageRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("존재하지 않는 채널에서 메시지 전송 시 예외가 발생한다.")
    void shouldThrowException_whenNonExistentChannel() {

        // given
        User user = createUser("테스터", "tester@codeit.com", "tester1234");
        UUID userId = user.getId();
        UUID channelId = UUID.randomUUID();
        String content = "메시지 생성 테스트입니다.";
        MessageCreateRequest createRequest = new MessageCreateRequest(content, userId, channelId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> {
            messageService.create(createRequest, new ArrayList<>());
        }).isInstanceOf(ChannelNotFoundException.class);

        then(userRepository).should().findById(userId);
        then(channelRepository).should().findById(channelId);
        then(binaryContentStorage).shouldHaveNoInteractions();
        then(binaryContentRepository).shouldHaveNoInteractions();
        then(messageRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("유효한 수정 요청으로 메시지 내용을 수정할 수 있다.")
    void shouldUpdateMessage_whenValidRequest() {

        // given
        User user = createUser("테스터", "tester@codeit.com", "tester1234");
        Channel channel = createChannel(ChannelType.PUBLIC, "테스트 채널", "테스트 채널입니다.");
        Message originalMsg = createMessage("테스트 메시지", channel, user);
        UUID messageId = originalMsg.getId();
        String newContent = "메시지 수정 테스트입니다.";
        MessageUpdateRequest updateRequest = new MessageUpdateRequest(newContent);
        Message updatedMsg = createMessage(newContent, channel, user);
        MessageDto messageDto = createMessageDto(updatedMsg, channel, user, fixedNow);

        given(messageRepository.findById(messageId)).willReturn(Optional.of(originalMsg));
        given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

        // when
        MessageDto result = messageService.update(messageId, updateRequest);

        // then
        assertThat(result.content()).isEqualTo(newContent);
    }

    @Test
    @DisplayName("존재하지 않는 메시지 수정 요청시 예외가 발생한다.")
    void shouldThrowException_whenNonExistentMessage() {

        // given
        UUID messageId = UUID.randomUUID();
        String newContent = "존재하지 않는 메시지 수정 테스트입니다.";
        MessageUpdateRequest updateRequest = new MessageUpdateRequest(newContent);

        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> {
            messageService.update(messageId, updateRequest);
        }).isInstanceOf(MessageNotFoundException.class);

        then(messageRepository).should().findById(messageId);
    }

    @Test
    @DisplayName("유효한 삭제 요청으로 메시지를 삭제할 수 있다.")
    void shouldDeleteMessage_whenValidRequest() {

        // given
        Channel channel = createChannel(ChannelType.PUBLIC, "테스트 채널", "테스트 채널입니다.");
        User user = createUser("테스터", "tester@codeit.com", "tester1234");
        Message message = createMessage("테스트 메시지", channel, user);
        UUID messageId = message.getId();

        given(messageRepository.existsById(messageId)).willReturn(true);
        willDoNothing().given(messageRepository).deleteById(messageId);

        // when
        messageService.delete(messageId);

        // then
        then(messageRepository).should().existsById(messageId);
        then(messageRepository).should().deleteById(messageId);
    }

    @Test
    @DisplayName("존재하지 않는 메시지를 삭제 요청시 예외가 발생한다.")
    void shouldThrowException_whenDeletingNonExistentMessage() {

        // given
        UUID messageId = UUID.randomUUID();

        given(messageRepository.existsById(messageId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> {
            messageService.delete(messageId);
        }).isInstanceOf(MessageNotFoundException.class);

        then(messageRepository).should().existsById(messageId);
        then(messageRepository).should(never()).deleteById(messageId);
    }

    @Test
    @DisplayName("커서가 있는 경우, 해당 시각 이전의 메시지들만 조회된다.")
    void shouldReturnMessagesBeforeCursor_whenCursorIsProvided() {

        // given
        User user = createUser("테스터", "tester@codeit.com", "test1234");
        Channel channel = createChannel(ChannelType.PUBLIC, "테스트 채널", "테스트 채널입니다.");
        UUID channelId = channel.getId();
        Pageable pageable = PageRequest.of(0, 10);
        Message msg1 = createMessage("메시지1", channel, user);
        Message msg2 = createMessage("메시지2", channel, user);
        Instant msg1Time = fixedNow.minusSeconds(30);
        Instant msg2Time = fixedNow.minusSeconds(10);
        Instant targetTime = fixedNow.minusSeconds(20);
        List<Message> messages = List.of(msg1);
        List<MessageDto> messageDtos = List.of(
                createMessageDto(msg1, channel, user, msg1Time)
        );
        Slice<Message> messageSlice = new SliceImpl<>(messages, pageable, true);

        given(channelRepository.existsById(channelId)).willReturn(true);
        given(messageRepository.findAllByChannelIdWithAuthor(eq(channelId), any(),
                eq(pageable))).willReturn(messageSlice);
        given(messageMapper.toDto(eq(msg1))).willReturn(messageDtos.get(0));
        given(pageResponseMapper.fromSlice(
                ArgumentMatchers.<Slice<MessageDto>>any(),
                any()
        )).willReturn(new PageResponse<>(
                messageDtos,
                messageDtos.get(0).createdAt(),
                messageDtos.size(),
                true,
                null
        ));

        // when
        PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, targetTime,
                pageable);

        // then
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).content()).isEqualTo("메시지1");
        assertThat(result.nextCursor()).isEqualTo(messageDtos.get(0).createdAt());

        then(messageRepository).should().findAllByChannelIdWithAuthor(eq(channelId), eq(targetTime),
                eq(pageable));
        then(messageMapper).should().toDto(any());
        then(pageResponseMapper).should().fromSlice(any(), any());
    }

    @Test
    @DisplayName("커서가 없는 경우, 현재 시각 기준으로 메시지들을 조회한다.")
    void shouldReturnMessagesBeforeCurrentTime_whenCursorNotProvided() {

        // given
        User user = createUser("테스터", "tester@codeit.com", "tester1234");
        Channel channel = createChannel(ChannelType.PUBLIC, "테스트 채널", "테스트 채널입니다.");
        UUID channelId = channel.getId();
        Pageable pageable = PageRequest.of(0, 10);
        Message msg1 = createMessage("메시지1", channel, user);
        Message msg2 = createMessage("메시지2", channel, user);
        Instant msg1Time = fixedNow.minusSeconds(30);
        Instant msg2Time = fixedNow.minusSeconds(10);
        List<Message> messages = List.of(msg1, msg2);
        List<MessageDto> messageDtos = List.of(
                createMessageDto(msg1, channel, user, msg1Time),
                createMessageDto(msg2, channel, user, msg2Time)
        );
        Slice<Message> messageSlice = new SliceImpl<>(messages, pageable, true);

        given(channelRepository.existsById(channelId)).willReturn(true);
        given(messageRepository.findAllByChannelIdWithAuthor(eq(channelId), any(),
                eq(pageable))).willReturn(messageSlice);
        given(messageMapper.toDto(eq(msg1))).willReturn(messageDtos.get(0));
        given(messageMapper.toDto(eq(msg2))).willReturn(messageDtos.get(1));
        given(pageResponseMapper.fromSlice(
                ArgumentMatchers.<Slice<MessageDto>>any(),
                any()
        )).willReturn(new PageResponse<>(
                messageDtos,
                messageDtos.get(1).createdAt(),
                messageDtos.size(),
                true,
                null
        ));

        // when
        PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, null,
                pageable);

        // then
        assertThat(result.content()).hasSize(2);
        assertThat(result.content().get(0).content()).isEqualTo("메시지1");
        assertThat(result.nextCursor()).isEqualTo(messageDtos.get(1).createdAt());

        then(messageRepository).should()
                .findAllByChannelIdWithAuthor(eq(channelId), any(), eq(pageable));
        then(messageMapper).should(times(2)).toDto(any());
        then(pageResponseMapper).should().fromSlice(any(), any());
    }

    @Test
    @DisplayName("존재하지 않는 채널로 조회 요청시 예외가 발생한다.")
    void shouldThrowException_whenFindingNonExistentChannel() {

        // given
        UUID channelId = UUID.randomUUID();

        given(channelRepository.existsById(channelId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> {
            messageService.findAllByChannelId(channelId, null, PageRequest.of(0, 10));
        }).isInstanceOf(ChannelNotFoundException.class);

        then(channelRepository).should().existsById(channelId);
        then(messageRepository).shouldHaveNoInteractions();
    }
}