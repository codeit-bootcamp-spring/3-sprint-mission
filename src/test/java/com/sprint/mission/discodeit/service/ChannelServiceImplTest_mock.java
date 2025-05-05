package com.sprint.mission.discodeit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.common.exception.ChannelException;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.data.PrivateChannelResponse;
import com.sprint.mission.discodeit.dto.data.PublicChannelResponse;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasicChannelService 단위 테스트")
class ChannelServiceImplTest_mock {

  private static final Logger log = LogManager.getLogger(ChannelServiceImplTest_mock.class);

  @Mock
  ChannelRepository channelRepository;
  @Mock
  MessageRepository messageRepository;
  @Mock
  ReadStatusRepository readStatusRepository;

  @InjectMocks
  ChannelServiceImpl channelService;

  @Test
  @DisplayName("공개 채널 생성 시 ChannelRepository의 save가 호출되고 PUBLIC 타입의 채널이 생성된다")
  void shouldCreatePublic() {
    UUID creatorId = UUID.randomUUID();
    PublicChannelCreateRequest request = new PublicChannelCreateRequest(creatorId, "general",
        "welcome");

    when(channelRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    Channel result = channelService.createPublic(request);

    assertEquals("general", result.getName());
    assertEquals(Channel.ChannelType.PUBLIC, result.getType());
    verify(channelRepository).save(any());
  }

  @Test
  @DisplayName("비공개 채널 생성 시 참여자가 추가되고 ReadStatus도 함께 생성된다")
  void shouldCreatePrivateChannelWithParticipantsAndReadStatus() {
    UUID creatorId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(creatorId,
        List.of(userId));

    when(channelRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(readStatusRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    Channel result = channelService.createPrivate(request);

    assertTrue(result.isParticipant(userId));
    verify(readStatusRepository).save(any());
    verify(channelRepository).save(result);
  }

  @Test
  @DisplayName("채널 조회는 Public/Private 채널을 구분해서 적절한 응답을 반환해야 한다")
  void searchChannels_shouldReturnProperResponseForEachChannelType() {
    UUID creatorId = UUID.randomUUID();
    UUID participantId = UUID.randomUUID();

    // Private 채널 세팅
    Channel privateChannel = Channel.createPrivate(creatorId);
    privateChannel.addParticipant(creatorId);  // 생성자 추가
    privateChannel.addParticipant(participantId);
    Message privateMessage = Message.create("Private 메시지", creatorId, privateChannel.getId());

    // Public 채널 세팅
    Channel publicChannel = Channel.createPublic(creatorId, "공지", "공지사항입니다");
    publicChannel.addParticipant(creatorId);
    Message publicMessage = Message.create("Public 메시지", creatorId, publicChannel.getId());

    when(channelRepository.findAll()).thenReturn(List.of(privateChannel, publicChannel));
    when(messageRepository.findAll()).thenReturn(List.of(privateMessage, publicMessage));

    List<ChannelResponse> result = channelService.findByCreatorIdOrName(null, null);

    assertEquals(2, result.size());

    for (ChannelResponse response : result) {
      assertTrue(response.latestMessageTime() != null);

      if (response instanceof PrivateChannelResponse privateResp) {
        // PRIVATE 채널일 때만 참여자 정보 검증
        assertEquals(privateChannel.getId(), privateResp.id());
        assertTrue(privateResp.participantIds().contains(creatorId));
        assertTrue(privateResp.participantIds().contains(participantId));
      } else if (response instanceof PublicChannelResponse publicResp) {
        // PUBLIC 채널은 참여자 정보가 없음 (publicResp에는 participantIds 없음)
        assertEquals(publicChannel.getId(), publicResp.id());
      } else {
        throw new AssertionError("Unknown ChannelResponse type: " + response.getClass());
      }
    }
  }

  @Test
  @DisplayName("findAllByUserId는 Public 채널과 참여한 Private 채널만 반환해야 한다")
  void findAllByUserId_shouldReturnOnlyAccessibleChannels() {
    UUID creatorId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UUID otherUserId = UUID.randomUUID();

    // Private 채널 (유저 참여)
    Channel privateChannel1 = Channel.createPrivate(creatorId);
    privateChannel1.addParticipant(userId);

    // Private 채널 (유저 불참)
    Channel privateChannel2 = Channel.createPrivate(creatorId);
    privateChannel2.addParticipant(otherUserId);

    // Public 채널
    Channel publicChannel = Channel.createPublic(creatorId, "public", "desc");

    when(channelRepository.findAll()).thenReturn(
        List.of(privateChannel1, privateChannel2, publicChannel));
    when(messageRepository.findAll()).thenReturn(List.of());

    List<ChannelResponse> result = channelService.findAllByUserId(userId);

    assertEquals(2, result.size());
    assertTrue(result.stream().anyMatch(r -> r.id().equals(publicChannel.getId())));
    assertTrue(result.stream().anyMatch(r -> r.id().equals(privateChannel1.getId())));
  }

  @Test
  @DisplayName("비공개 채널의 이름을 업데이트하려고 하면 ChannelException이 발생한다")
  void shouldThrowWhenUpdatingPrivateChannelName() {
    UUID channelId = UUID.randomUUID();
    Channel privateChannel = Channel.createPrivate(UUID.randomUUID());

    when(channelRepository.findById(channelId)).thenReturn(Optional.of(privateChannel));

    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(channelId, "new-name",
        null);

    assertThrows(ChannelException.class,
        () -> channelService.update(request));
  }

  @Test
  @DisplayName("공개 채널의 이름을 업데이트하면 채널 이름이 변경된다")
  void shouldUpdatePublicChannelName() {
    UUID channelId = UUID.randomUUID();
    Channel publicChannel = Channel.createPublic(UUID.randomUUID(), "old-name", "desc");

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

  @Test
  @DisplayName("채널 삭제 시 채널과 관련된 Message, ReadStatus를 함께 삭제해야 한다")
  void deleteChannel_shouldAlsoDeleteMessagesAndReadStatuses() {
    UUID channelId = UUID.randomUUID();
    Channel channel = Channel.createPublic(UUID.randomUUID(), "general", "desc");

    Message message = Message.create("hello", UUID.randomUUID(), channelId);
    ReadStatus status = ReadStatus.create(UUID.randomUUID(), channelId);

    when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
    when(messageRepository.findAll()).thenReturn(List.of(message));
    when(readStatusRepository.findByChannelId(channelId)).thenReturn(List.of(status));

    Optional<ChannelResponse> result = channelService.delete(channelId);

    assertTrue(result.isPresent());
    verify(messageRepository).delete(message.getId());
    verify(readStatusRepository).delete(status.getId());
    verify(channelRepository).delete(channelId);
  }
}
