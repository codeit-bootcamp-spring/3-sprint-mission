package com.sprint.mission.discodeit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.MessageFixture;
import com.sprint.mission.discodeit.fixture.ReadStatusFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasicChannelService 단위 테스트")
class BasicChannelServiceTest {

  private static final Logger log = LogManager.getLogger(BasicChannelServiceTest.class);

  @Mock
  ChannelRepository channelRepository;
  @Mock
  MessageRepository messageRepository;
  @Mock
  ReadStatusRepository readStatusRepository;

  @InjectMocks
  BasicChannelService channelService;

  private User user;
  private Channel publicChannel;
  private Channel privateChannel;
  private ReadStatus readStatus;
  private Message message;
  private PublicChannelCreateRequest publicRequest;
  private PrivateChannelCreateRequest privateRequest;

  @BeforeEach
  void setUp() {
    user = UserFixture.createValidUser();
    publicChannel = ChannelFixture.createPublic();
    privateChannel = ChannelFixture.createPrivate();
    readStatus = ReadStatusFixture.create();
    publicRequest = new PublicChannelCreateRequest(publicChannel.getName(),
        publicChannel.getDescription());
    privateRequest = new PrivateChannelCreateRequest(List.of(user.getId()));
  }

  @Nested
  class Create {

    @Test
    void 공개_채널_생성_시_PUBLIC_타입의_채널이_생성된다() {
      when(channelRepository.save(any(Channel.class))).thenReturn(publicChannel);

      ChannelResponse result = channelService.create(publicRequest);

      assertEquals(publicRequest.name(), result.name());
      assertEquals(ChannelType.PUBLIC, result.type());
      verify(channelRepository).save(any());
    }

    @Test
    void 비공개_채널_생성_시_참여자의_ReadStatus도_함께_생성된다() {
      when(readStatusRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
      when(channelRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

      ChannelResponse result = channelService.create(privateRequest);

      verify(readStatusRepository).save(any());
      verify(channelRepository).save(any(Channel.class));
    }
  }

  @Nested
  class Read {

    @Test
    void 채널_조회는_채널_타입을_구분해서_적절한_응답을_반환해야_한다() {
      ReadStatus rs1 = ReadStatusFixture.createFromRequest(
          new ReadStatusCreateRequest(user.getId(), privateChannel.getId()));
      ReadStatus rs2 = ReadStatusFixture.createFromRequest(
          new ReadStatusCreateRequest(user.getId(), publicChannel.getId()));
      Message ms1 = MessageFixture.createCustom(
          new MessageCreateRequest("testMessage", user.getId(), privateChannel.getId()));
      Message ms2 = MessageFixture.createCustom(
          new MessageCreateRequest("testMessage", user.getId(), publicChannel.getId()));

      when(readStatusRepository.findAllByUserId(any())).thenReturn(List.of(rs1, rs2));
      when(channelRepository.findAll()).thenReturn(List.of(privateChannel, publicChannel));
      when(messageRepository.findAll()).thenReturn(List.of(ms1, ms2));
      when(readStatusRepository.findAllByChannelId(privateChannel.getId())).thenReturn(
          List.of(rs1));

      List<ChannelResponse> result = channelService.findAllByUserId(user.getId());

      assertEquals(2, result.size());

      for (ChannelResponse response : result) {
        assertNotNull(response.lastMessageAt());

        if (response.type() == ChannelType.PRIVATE) {
          assertEquals(privateChannel.getId(), response.id());
          assertTrue(response.participantIds().contains(user.getId()));
          assertNotNull(response.lastMessageAt());
        } else {
          assertEquals(publicChannel.getId(), response.id());
        }
      }

      verify(readStatusRepository).findAllByUserId(any());
    }
  }

  @Nested
  class Update {

    @Test
    void 비공개_채널의_이름을_업데이트하려고_하면_ChannelException이_발생한다() {
      UUID channelId = UUID.randomUUID();
      Channel privateChannel = Channel.createPrivate();

      when(channelRepository.findById(channelId)).thenReturn(Optional.of(privateChannel));

      PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(channelId, "new-name",
          null);

      assertThrows(ChannelException.class,
          () -> channelService.update(request));
    }

    @Test
    void 공개_채널의_이름을_업데이트하면_채널_이름이_변경된다() {
      UUID channelId = UUID.randomUUID();
      Channel publicChannel = Channel.createPublic("old-name", "desc");

      when(channelRepository.findById(channelId)).thenReturn(Optional.of(publicChannel));
      when(channelRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

      String newName = "new-name";
      String description = "new-desc";
      PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(channelId, newName,
          description);

      Optional<ChannelResponse> result = channelService.update(request);

      assertTrue(result.isPresent());
      assertEquals(newName, result.get().name());
      assertEquals(description, result.get().description());
      verify(channelRepository).save(any());
    }
  }

  @Nested
  class Delete {

    @Test
    void 채널_삭제_시_채널과_관련된_도메인의_데이터를_함께_삭제해야_한다() {
      UUID channelId = UUID.randomUUID();
      Channel channel = Channel.createPublic("general", "desc");

      Message message = Message.create("hello", UUID.randomUUID(), channelId, null);
      ReadStatus status = ReadStatus.create(UUID.randomUUID(), channelId);

      when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
      when(messageRepository.findAll()).thenReturn(List.of(message));
      when(readStatusRepository.findAllByChannelId(channelId)).thenReturn(List.of(status));

      Optional<ChannelResponse> result = channelService.delete(channelId);

      assertTrue(result.isPresent());
      verify(messageRepository).delete(message.getId());
      verify(readStatusRepository).delete(status.getId());
      verify(channelRepository).delete(channelId);
    }
  }
}
