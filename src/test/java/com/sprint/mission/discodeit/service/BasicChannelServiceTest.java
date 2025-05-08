package com.sprint.mission.discodeit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PrivateChannelResponse;
import com.sprint.mission.discodeit.dto.response.PublicChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

  @Nested
  class Create {

    @Test
    void 공개_채널_생성_시_PUBLIC_타입의_채널이_생성된다() {
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
    void 비공개_채널_생성_시_참여자가_추가되고_ReadStatus도_함께_생성된다() {
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
  }

  @Nested
  class Read {

    @Test
    void 채널_조회는_채널_타입을_구분해서_적절한_응답을_반환해야_한다() {
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
    void findAllByUserId는_참여한_채널만_반환해야_한다() {
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
  }

  @Nested
  class Update {

    @Test
    void 비공개_채널의_이름을_업데이트하려고_하면_ChannelException이_발생한다() {
      UUID channelId = UUID.randomUUID();
      Channel privateChannel = Channel.createPrivate(UUID.randomUUID());

      when(channelRepository.findById(channelId)).thenReturn(Optional.of(privateChannel));

      PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(channelId, "new-name",
          null);

      assertThrows(ChannelException.class,
          () -> channelService.update(request));
    }

    @Test
    void 공개_채널의_이름을_업데이트하면_채널_이름이_변경된다() {
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
  }

  @Nested
  class Delete {

    @Test
    void 채널_삭제_시_채널과_관련된_도메인의_데이터를_함께_삭제해야_한다() {
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
}
