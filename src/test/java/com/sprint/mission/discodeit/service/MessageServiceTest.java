package com.sprint.mission.discodeit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.Common.ResourceNotFoundException;
import com.sprint.mission.discodeit.exception.Message.AccessDeniedMessageException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.function.Executable;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class MessageServiceTest {

  private MessageService messageService;
  private final MessageRepository messageRepository = mock(MessageRepository.class);
  private final ChannelRepository channelRepository = mock(ChannelRepository.class);
  private final ReadStatusRepository readStatusRepository = mock(ReadStatusRepository.class);
  private final UserRepository userRepository = mock(UserRepository.class);
  private final BinaryContentService binaryContentService = mock(BinaryContentService.class);
  private final BinaryContentStorage binaryContentStorage = mock(BinaryContentStorage.class);
  private final BinaryContentRepository binaryContentRepository = mock(
      BinaryContentRepository.class);
  private final MessageMapper messageMapper = mock(MessageMapper.class);
  private final PageResponseMapper pageResponseMapper = mock(PageResponseMapper.class);

  @BeforeEach
  void setUp() {
    messageService = new BasicMessageService(
        messageRepository, channelRepository, readStatusRepository,
        userRepository, binaryContentService, binaryContentStorage,
        binaryContentRepository, messageMapper, pageResponseMapper);
  }

  @Nested
  @DisplayName("메시지 생성 테스트")
  class 메시지생성 {

    @Test
    @DisplayName("유효한 요청으로 메시지 생성에 성공한다")
    void whenCreateMessage_thenReturnMessageDto() {
      // Given
      UUID authorId = UUID.randomUUID();
      UUID channelId = UUID.randomUUID();
      MessageCreateRequest request = new MessageCreateRequest("hello", authorId, channelId);
      User user = new User("test", "test@mail.com", "pw", null);
      UserDto userDto = new UserDto(UUID.randomUUID(), "test", "test@mail.com",
          null, true,
          Instant.now(), Instant.now());
      Channel channel = new Channel();
      Message message = new Message(user, channel, request.content(), List.of());
      MessageDto expected = new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(),
          request.content(), channelId, userDto, List.of());
      given(userRepository.findById(authorId)).willReturn(Optional.of(user));
      given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
      given(readStatusRepository.existsByUserIdAndChannelId(authorId, channelId)).willReturn(true);
      given(messageRepository.save(any())).willReturn(message);
      given(messageMapper.toDto(any())).willReturn(expected);

      // When
      MessageDto result = messageService.create(request, List.of());

      // Then
      then(messageRepository).should().save(any());
      then(messageMapper).should().toDto(any());
      assertEquals(expected, result);
    }

    @Test
    @DisplayName("채널에 속하지 않은 유저는 메시지 생성에 실패한다")
    void whenUserNotInChannel_thenThrowAccessDenied() {
      // Given
      UUID authorId = UUID.randomUUID();
      UUID channelId = UUID.randomUUID();
      MessageCreateRequest request = new MessageCreateRequest("fail", authorId, channelId);
      User user = new User("test", "test@mail.com", "pw", null);
      Channel channel = new Channel();
      given(userRepository.findById(authorId)).willReturn(Optional.of(user));
      given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
      given(readStatusRepository.existsByUserIdAndChannelId(authorId, channelId)).willReturn(false);
      // When
      Executable executable = () -> messageService.create(request, List.of());

      // Then
      assertThrows(AccessDeniedMessageException.class, executable);
      then(messageRepository).shouldHaveNoInteractions();
    }
  }

  @Nested
  @DisplayName("메시지 조회 테스트")
  class 메시지조회 {

    @Test
    @DisplayName("ID로 메시지 조회 성공")
    void whenFindById_thenReturnDto() {
      // Given
      UUID messageId = UUID.randomUUID();
      Message message = new Message();
      UserDto userDto = new UserDto(UUID.randomUUID(), "test", "test@mail.com",
          null, true,
          Instant.now(), Instant.now());
      MessageDto expected = new MessageDto(messageId, Instant.now(), Instant.now(), "msg",
          UUID.randomUUID(), userDto,
          List.of());
      given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
      given(messageMapper.toDto(message)).willReturn(expected);

      // When
      MessageDto result = messageService.findById(messageId);

      // Then
      then(messageRepository).should().findById(messageId);
      assertEquals(expected, result);
    }

    @Test
    @DisplayName("존재하지 않는 메시지 ID 조회 시 예외 발생")
    void whenFindByIdNotFound_thenThrowException() {
      // Given
      UUID messageId = UUID.randomUUID();
      given(messageRepository.findById(messageId)).willReturn(Optional.empty());

      // When
      Executable executable = () -> messageService.findById(messageId);

      // Then
      assertThrows(ResourceNotFoundException.class, executable);
    }
  }

  @Nested
  @DisplayName("메시지 업데이트 테스트")
  class 메시지수정 {

    @Test
    @DisplayName("메시지 내용 수정 성공")
    void whenUpdate_thenReturnUpdatedDto() {
      // Given
      UUID messageId = UUID.randomUUID();
      MessageUpdateRequest request = new MessageUpdateRequest("updated");
      Message message = new Message();
//      Message message = new Message(user, channel, request.content(), List.of());
      UserDto userDto = new UserDto(UUID.randomUUID(), "test", "test@mail.com",
          null, true,
          Instant.now(), Instant.now());
      MessageDto expected = new MessageDto(messageId, Instant.now(), Instant.now(),
          request.newContent(), UUID.randomUUID(),
          userDto, List.of());
      given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
      given(messageMapper.toDto(message)).willReturn(expected);

      // When
      MessageDto result = messageService.update(messageId, request);

      // Then
      then(messageRepository).should().findById(messageId);
      assertEquals(expected, result);
    }

    @Test
    @DisplayName("존재하지 않는 메시지 수정 시 예외 발생")
    void whenUpdateNonexistent_thenThrowException() {
      // Given
      UUID messageId = UUID.randomUUID();
      MessageUpdateRequest request = new MessageUpdateRequest("fail");
      given(messageRepository.findById(messageId)).willReturn(Optional.empty());

      // When
      Executable executable = () -> messageService.update(messageId, request);

      // Then
      assertThrows(ResourceNotFoundException.class, executable);
    }
  }

  @Nested
  @DisplayName("메시지 삭제 테스트")
  class 메시지삭제 {

    @Test
    @DisplayName("메시지 삭제 성공")
    void whenDelete_thenSuccess() {
      // Given
      UUID messageId = UUID.randomUUID();
      Message message = new Message();
      given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
      given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

      // When
      messageService.delete(messageId);

      // Then
      then(messageRepository).should().deleteById(messageId);
    }

    @Test
    @DisplayName("존재하지 않는 메시지 삭제 시 예외 발생")
    void whenDeleteNonexistent_thenThrowException() {
      // Given
      UUID messageId = UUID.randomUUID();
      given(messageRepository.findById(messageId)).willReturn(Optional.empty());

      // When
      Executable executable = () -> messageService.delete(messageId);

      // Then
      assertThrows(ResourceNotFoundException.class, executable);
    }
  }
}
