package com.sprint.mission.discodeit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.Channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.Common.ResourceNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.function.Executable;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class ChannelServiceTest {


  private ChannelService channelService;
  private ChannelRepository channelRepository = mock(ChannelRepository.class);
  private MessageRepository messageRepository = mock(MessageRepository.class);
  private ReadStatusRepository readStatusRepository = mock(ReadStatusRepository.class);
  private UserRepository userRepository = mock(UserRepository.class);
  private ChannelMapper channelMapper = mock(ChannelMapper.class);


  @BeforeEach
  void setUp() {
    channelService = new BasicChannelService(channelRepository, messageRepository,
        readStatusRepository, userRepository, channelMapper);
  }

  @AfterEach
  void tearDown() {
    //로그 또는 자원 해제 처리
  }

  @Nested
  @DisplayName("채널 생성 테스트")
  class 채널생성 {

    @DisplayName("공개 채널 생성에 성공한다")
    @Test
    void whenCreatePublicChannel_thenReturnChannelDto() {
      // Given
      PublicChannelCreateRequest request = new PublicChannelCreateRequest("public channel", "desc",
          UUID.randomUUID());
      Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description(),
          request.ownerId());
      UserDto userDto = new UserDto(UUID.randomUUID(), "test", "test@naver.com",
          null, true,
          Instant.now(), Instant.now());
      ChannelDto expectedDto = new ChannelDto(channel.getId(), ChannelType.PUBLIC, request.name(),
          request.description(), List.of(userDto), Instant.now());
      given(channelRepository.save(any(Channel.class))).willReturn(channel);
      given(channelMapper.toDto(any(Channel.class))).willReturn(expectedDto);

      // When
      ChannelDto result = channelService.create(request);

      // Then
      then(channelRepository).should().save(any(Channel.class));
      then(channelMapper).should().toDto(any(Channel.class));
      assertEquals(expectedDto, result);
    }

    @DisplayName("비공개 채널 생성에 성공한다")
    @Test
    void whenCreatePrivateChannel_thenReturnChannelDto() {
      // Given
      UUID memberId = UUID.randomUUID();
      PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(memberId));
      Channel channel = new Channel(ChannelType.PRIVATE, null, null, null);
      User owner = new User("owner", "owner@mail.com", "pw", null);
      User participant = new User("mem", "mem@mail.com", "pw", null);
      UserDto userDto = new UserDto(UUID.randomUUID(), "test", "test@naver.com",
          null, true,
          Instant.now(), Instant.now());
      ChannelDto expectedDto = new ChannelDto(channel.getId(), ChannelType.PUBLIC, null,
          null, List.of(userDto), Instant.now());
      given(channelRepository.save(any())).willReturn(channel);
      given(userRepository.findAllById(List.of(memberId))).willReturn(List.of(participant));
      given(userRepository.findById(memberId)).willReturn(Optional.of(owner));
      given(readStatusRepository.saveAll(any())).willReturn(List.of());
      given(channelMapper.toDto(channel)).willReturn(expectedDto);

      // When
      ChannelDto result = channelService.create(request);

      // Then
      //FIXME
      then(channelRepository).should().save(any());
      then(readStatusRepository).should().saveAll(any());
      then(channelMapper).should().toDto(any(Channel.class));
    }


  }

  @Nested
  @DisplayName("채널 찾기 테스트")
  class 채널찾기 {

    @DisplayName("채널 ID로 조회 성공")
    @Test
    void whenFindChannel_thenReturnChannelDto() {
      // Given
      UUID id = UUID.randomUUID();
      Channel channel = new Channel(ChannelType.PUBLIC, "name", "desc", UUID.randomUUID());
      UserDto userDto = new UserDto(UUID.randomUUID(), "test", "test@naver.com",
          null, true,
          Instant.now(), Instant.now());
      ChannelDto expectedDto = new ChannelDto(id, ChannelType.PUBLIC, "name", "desc",
          List.of(userDto), Instant.now());
      given(channelRepository.findById(id)).willReturn(Optional.of(channel));
      given(channelMapper.toDto(channel)).willReturn(expectedDto);

      // When
      ChannelDto result = channelService.find(id);

      // Then
      then(channelRepository).should().findById(id);
      assertEquals(expectedDto, result);
    }

    @DisplayName("유저 ID로 속한 채널 전체 조회 성공")
    @Test
    void whenFindAllByUserId_thenReturnList() {
      // Given
      UUID userId = UUID.randomUUID();
      Channel channel = new Channel(ChannelType.PUBLIC, "public", "desc", UUID.randomUUID());
      UserDto userDto = new UserDto(UUID.randomUUID(), "test", "test@naver.com",
          null, true,
          Instant.now(), Instant.now());
      ChannelDto dto = new ChannelDto(channel.getId(), ChannelType.PUBLIC, "public", "desc",
          List.of(userDto), Instant.now());
      ReadStatus readStatus = new ReadStatus(new User(), channel, Instant.now());
      given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of(readStatus));
      given(channelRepository.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), any())).willReturn(
          List.of(channel));
      given(channelMapper.toDto(channel)).willReturn(dto);

      // When
      List<ChannelDto> result = channelService.findAllByUserId(userId);

      // Then
      then(readStatusRepository).should().findAllByUserId(userId);
      then(channelRepository).should().findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), any());
      assertEquals(List.of(dto), result);
    }

  }


  @Nested
  @DisplayName("채널 업데이트 테스트")
  class 채널업데이트 {

    @DisplayName("공개 채널 정보 수정 성공")
    @Test
    void whenUpdatePublicChannel_thenReturnUpdatedDto() {
      // Given
      UUID id = UUID.randomUUID();
      PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("newName", "newDesc");
      Channel channel = new Channel(ChannelType.PUBLIC, "old", "old", UUID.randomUUID());
      UserDto userDto = new UserDto(UUID.randomUUID(), "test", "test@naver.com",
          null, true,
          Instant.now(), Instant.now());
      ChannelDto expectedDto = new ChannelDto(channel.getId(), ChannelType.PUBLIC, "newName",
          "newDesc", List.of(userDto), Instant.now());
      given(channelRepository.findById(id)).willReturn(Optional.of(channel));
      given(channelMapper.toDto(channel)).willReturn(expectedDto);

      // When
      ChannelDto result = channelService.update(id, request);

      // Then
      then(channelRepository).should().findById(id);
      assertEquals("newName", channel.getName()); // 실제 업데이트 여부 확인
      assertEquals(expectedDto, result);
    }

    @DisplayName("비공개 채널 수정 시 예외 발생")
    @Test
    void whenUpdatePrivateChannel_thenThrowException() {
      // Given
      UUID id = UUID.randomUUID();
      Channel privateChannel = new Channel(ChannelType.PRIVATE, "secret", "desc",
          UUID.randomUUID());
      given(channelRepository.findById(id)).willReturn(Optional.of(privateChannel));

      // when
      Executable executable = () -> channelService.update(id,
          new PublicChannelUpdateRequest("name", "desc"));

      // then
      assertThrows(PrivateChannelUpdateException.class, executable);

//      assertThrows(PrivateChannelUpdateException.class, () ->
//          channelService.update(id, new PublicChannelUpdateRequest("name", "desc"))
//      );
    }


  }


  @Nested
  @DisplayName("채널 삭제 테스트")
  class 채널삭제 {

    @DisplayName("채널 삭제 성공")
    @Test
    void whenDeleteChannel_thenRepositoriesCalled() {
      // Given
      UUID id = UUID.randomUUID();
      Channel channel = new Channel(ChannelType.PUBLIC, "c", "d", UUID.randomUUID());
      given(channelRepository.findById(id)).willReturn(Optional.of(channel));

      // when
      channelService.delete(id);

      // Then
      then(messageRepository).should().deleteAllByChannelId(channel.getId());
      then(readStatusRepository).should().deleteAllByChannelId(channel.getId());
      then(channelRepository).should().deleteById(channel.getId());
    }

    @DisplayName("존재하지 않는 채널 삭제 시 예외 발생")
    @Test
    void whenDeleteNonexistentChannel_thenThrowException() {
      // Given
      UUID id = UUID.randomUUID();
      given(channelRepository.findById(id)).willReturn(Optional.empty());

      // When
      Executable executable = () -> channelService.delete(id);

      // Then
      assertThrows(ResourceNotFoundException.class, executable);

//      assertThrows(ResourceNotFoundException.class, () -> channelService.delete(id));

    }


  }

}


