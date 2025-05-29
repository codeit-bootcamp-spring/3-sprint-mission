package com.sprint.mission.discodeit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
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
import com.sprint.mission.discodeit.repository.UserRepository;
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
  UserRepository userRepository;
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

  @BeforeEach
  void setUp() {
    user = UserFixture.createValidUserWithId();
    publicChannel = ChannelFixture.createPublic();
    privateChannel = ChannelFixture.createPrivate();
  }

  @Nested
  class Create {

    @Test
    void 공개_채널_생성_시_PUBLIC_타입의_채널이_생성된다() {
      String name = "테스트 채널";
      String description = "테스트 채널임";

      when(channelRepository.save(any(Channel.class))).thenReturn(publicChannel);

      ChannelResponse result = channelService.create(name, description);

      assertEquals(name, result.name());
      assertEquals(ChannelType.PUBLIC, result.type());
      verify(channelRepository).save(any());
    }

    @Test
    void 비공개_채널_생성_시_참여자의_ReadStatus도_함께_생성된다() {
      when(readStatusRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
      when(channelRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
      when(userRepository.findById(any())).thenReturn(Optional.of(user));

      channelService.create(List.of(user.getId()));

      verify(readStatusRepository).save(any());
      verify(channelRepository).save(any(Channel.class));
    }
  }

  @Nested
  class Read {

    @Test
    void 채널_조회는_채널_타입을_구분해서_적절한_응답을_반환해야_한다() {
      ReadStatus readStatus = ReadStatusFixture.create(user, privateChannel);

      Message privateMessage = MessageFixture.createCustom("testMessage", user, privateChannel);
      Message publicMessage = MessageFixture.createCustom("testMessage", user, publicChannel);

      when(messageRepository.findAll()).thenReturn(List.of(privateMessage, publicMessage));
      when(channelRepository.findAllByUserId(user.getId())).thenReturn(
          List.of(privateChannel, publicChannel));
      when(readStatusRepository.findAllByChannelId(privateChannel.getId())).thenReturn(
          List.of(readStatus));

      List<ChannelResponse> result = channelService.findAllByUserId(user.getId());

      assertEquals(2, result.size());

      for (ChannelResponse response : result) {
        assertNotNull(response.lastMessageAt());

        if (response.type() == ChannelType.PRIVATE) {
          assertEquals(privateChannel.getId(), response.id());
          assertTrue(response.participants().contains(user));
          assertNotNull(response.lastMessageAt());
        } else {
          assertEquals(publicChannel.getId(), response.id());
        }
      }

      verify(readStatusRepository).findAllByChannelId(any());
    }
  }

  @Nested
  class Update {

    @Test
    void 비공개_채널의_이름을_업데이트하려고_하면_ChannelException이_발생한다() {
      UUID channelId = UUID.randomUUID();
      Channel privateChannel = Channel.createPrivate();

      when(channelRepository.findById(channelId)).thenReturn(Optional.of(privateChannel));

      PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("new-username",
          null);

      assertThrows(ChannelException.class,
          () -> channelService.update(
              channelId,
              request.newName(),
              request.newDescription()
          ));
    }

    @Test
    void 공개_채널의_이름을_업데이트하면_채널_이름이_변경된다() {
      UUID channelId = UUID.randomUUID();
      Channel publicChannel = Channel.createPublic("old-username", "desc");

      when(channelRepository.findById(channelId)).thenReturn(Optional.of(publicChannel));
      when(channelRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

      String newName = "new-username";
      String newDescription = "new-desc";
      PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(newName,
          newDescription);

      ChannelResponse result = channelService.update(
          channelId,
          request.newName(),
          request.newDescription()
      );

      assertEquals(newName, result.name());
      assertEquals(newDescription, result.description());
      verify(channelRepository).save(any());
    }
  }

  @Nested
  class Delete {

    @Test
    void 채널_삭제_시_채널과_관련된_도메인의_데이터를_함께_삭제해야_한다() {
      UUID channelId = UUID.randomUUID();
      Channel channel = Channel.createPublic("general", "desc");

      Message message = MessageFixture.createCustom("testMessage", user, channel);
      ReadStatus status = ReadStatus.create(user, channel);

      when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
      when(messageRepository.findAll()).thenReturn(List.of(message));
      when(readStatusRepository.findAllByChannelId(channelId)).thenReturn(List.of(status));

      ChannelResponse result = channelService.delete(channelId);

      verify(messageRepository).deleteById(message.getId());
      verify(readStatusRepository).deleteById(status.getId());
      verify(channelRepository).deleteById(channelId);
    }
  }
}
