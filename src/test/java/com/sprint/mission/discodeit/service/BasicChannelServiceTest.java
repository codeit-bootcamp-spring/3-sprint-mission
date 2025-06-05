package com.sprint.mission.discodeit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.assembler.ChannelAssembler;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.MessageFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasicChannelService 단위 테스트")
class BasicChannelServiceTest {

  @Mock
  UserRepository userRepository;
  @Mock
  ChannelRepository channelRepository;
  @Mock
  MessageRepository messageRepository;
  @Mock
  ReadStatusRepository readStatusRepository;
  @Mock
  ChannelAssembler channelAssembler;

  @InjectMocks
  BasicChannelService channelService;

  private User user;
  private Channel publicChannel;
  private Channel privateChannel;

  @BeforeEach
  void setUp() {
    user = UserFixture.createValidUserWithId();
    publicChannel = ChannelFixture.createPublic();
    privateChannel = ChannelFixture.createPrivate();

    Mockito.lenient().when(channelAssembler.toResponse(any(Channel.class)))
        .thenAnswer(invocation -> {
          Channel ch = invocation.getArgument(0);
          List<UserResponse> participants = List.of(new UserResponse(
              user.getId(), user.getUsername(), user.getEmail(), null, false));
          return new ChannelResponse(
              ch.getId(), ch.getType(), ch.getName(), ch.getDescription(),
              participants, Instant.now());
        });
  }

  @Nested
  @DisplayName("Create 테스트")
  class CreateTests {

    @Test
    @DisplayName("공개 채널 생성")
    void 공개채널_생성() {
      when(channelRepository.save(any(Channel.class))).thenReturn(publicChannel);

      ChannelResponse response = channelService.create("테스트 채널", "테스트 채널임");

      assertEquals("테스트 채널", response.name());
      assertEquals(ChannelType.PUBLIC, response.type());
      verify(channelRepository).save(any());
    }

    @Test
    @DisplayName("비공개 채널 생성 및 ReadStatus 생성")
    void 비공개채널_및_ReadStatus_생성() {
      when(readStatusRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
      when(channelRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
      when(userRepository.findById(any())).thenReturn(Optional.of(user));

      channelService.create(List.of(user.getId()));

      verify(readStatusRepository).save(any());
      verify(channelRepository).save(any(Channel.class));
    }
  }

  @Nested
  @DisplayName("Read 테스트")
  class ReadTests {

    @Test
    @DisplayName("유저별 채널 조회 및 응답 검증")
    void findAllByUserIdReturnsProperResponses() {
      when(channelRepository.findAllByUserId(user.getId())).thenReturn(
          List.of(privateChannel, publicChannel));

      List<ChannelResponse> responses = channelService.findAllByUserId(user.getId());

      assertEquals(2, responses.size());
      responses.forEach(this::verifyChannelResponse);
    }

    private void verifyChannelResponse(ChannelResponse response) {
      assertNotNull(response.lastMessageAt());
      if (response.type() == ChannelType.PRIVATE) {
        verifyPrivateChannelResponse(response);
      } else {
        verifyPublicChannelResponse(response);
      }
    }

    private void verifyPrivateChannelResponse(ChannelResponse response) {
      assertEquals(privateChannel.getId(), response.id());
      assertTrue(response.participants().stream()
          .anyMatch(p -> p.id().equals(user.getId())));
    }

    private void verifyPublicChannelResponse(ChannelResponse response) {
      assertEquals(publicChannel.getId(), response.id());
    }
  }

  @Nested
  @DisplayName("Update 테스트")
  class UpdateTests {

    @Test
    @DisplayName("비공개 채널 이름 변경 시 예외")
    void updatePrivateChannelNameThrowsException() {
      UUID channelId = UUID.randomUUID();
      Channel privateCh = Channel.createPrivate();
      when(channelRepository.findById(channelId)).thenReturn(Optional.of(privateCh));

      PublicChannelUpdateRequest req = new PublicChannelUpdateRequest("new-name", null);

      assertThrows(ChannelException.class,
          () -> channelService.update(channelId, req.newName(), req.newDescription()));
    }

    @Test
    @DisplayName("공개 채널 이름 및 설명 변경 성공")
    void updatePublicChannelName_success() {
      UUID channelId = UUID.randomUUID();
      Channel publicCh = Channel.createPublic("old-name", "old-desc");
      when(channelRepository.findById(channelId)).thenReturn(Optional.of(publicCh));
      when(channelRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

      PublicChannelUpdateRequest req = new PublicChannelUpdateRequest("new-name", "new-desc");

      ChannelResponse response = channelService.update(channelId, req.newName(),
          req.newDescription());

      assertEquals(req.newName(), response.name());
      assertEquals(req.newDescription(), response.description());
      verify(channelRepository).save(any());
    }
  }

  @Nested
  @DisplayName("Delete 테스트")
  class DeleteTests {

    @Test
    @DisplayName("채널 삭제 시 관련 메시지, ReadStatus, 채널 삭제 확인")
    void deleteChannelAlsoDeletesRelatedData() {
      UUID channelId = UUID.randomUUID();
      Channel channel = Channel.createPublic("general", "desc");
      Message message = MessageFixture.createCustom("msg", user, channel);
      ReadStatus status = ReadStatus.create(user, channel);

      when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
      when(messageRepository.findAll()).thenReturn(List.of(message));
      when(readStatusRepository.findAllByChannelId(channelId)).thenReturn(List.of(status));

      channelService.delete(channelId);

      verify(messageRepository).deleteById(message.getId());
      verify(readStatusRepository).deleteById(status.getId());
      verify(channelRepository).deleteById(channelId);
    }
  }
}
