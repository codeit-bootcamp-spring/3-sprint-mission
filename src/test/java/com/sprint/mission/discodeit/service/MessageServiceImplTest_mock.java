package com.sprint.mission.discodeit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.data.MessageResponse;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.MessageFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MessageServiceImplTest_mock {

  @Mock
  UserRepository userRepository;
  @Mock
  ChannelRepository channelRepository;
  @Mock
  MessageRepository messageRepository;
  @Mock
  BinaryContentRepository binaryContentRepository;

  @InjectMocks
  MessageServiceImpl messageService;

  @Nested
  @DisplayName("메시지 서비스 테스트")
  class Create {

    @Test
    @DisplayName("메시지를 생성하면 첨부파일 없이도 등록된다")
    void shouldCreateMessageWithoutAttachments() {
      UUID userId = UUID.randomUUID();
      UUID channelId = UUID.randomUUID();
      String content = "메시지 내용";

      MessageCreateRequest request = new MessageCreateRequest(content, userId, channelId,
          Optional.empty());
      Channel channel = ChannelFixture.createValidChannel();
      channel.addParticipant(userId); // 유저를 명시적으로 추가

      when(userRepository.findById(userId)).thenReturn(Optional.of(UserFixture.createValidUser()));
      when(channelRepository.findById(channelId)).thenReturn(
          Optional.of(channel));
      when(messageRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

      Message result = messageService.create(request);

      assertNotNull(result.getId());
      verify(messageRepository).save(any());
      verifyNoInteractions(binaryContentRepository);
    }

    @Test
    @DisplayName("첨부파일을 메시지에 연결하면 BinaryContent의 messageId가 업데이트된다")
    void shouldAttachFilesToMessage() {
      UUID messageId = UUID.randomUUID();
      UUID attachmentId = UUID.randomUUID();
      BinaryContent attachment = BinaryContentFixture.createValidMessageAttachment();

      when(messageRepository.findById(messageId)).thenReturn(
          Optional.of(MessageFixture.createValidMessage()));
      when(binaryContentRepository.findById(attachmentId)).thenReturn(Optional.of(attachment));

      messageService.attachFilesToMessage(messageId, List.of(attachmentId));

      verify(binaryContentRepository).save(any());
    }

    @Test
    @DisplayName("메시지를 생성할 때 첨부파일도 함께 등록된다")
    void shouldCreateMessageWithAttachments() {
      UUID userId = UUID.randomUUID();
      UUID channelId = UUID.randomUUID();
      UUID attachmentId1 = UUID.randomUUID();
      UUID attachmentId2 = UUID.randomUUID();

      MessageCreateRequest request = new MessageCreateRequest(
          "첨부파일 메시지",
          userId,
          channelId,
          Optional.of(Set.of(attachmentId1, attachmentId2))
      );

      Channel channel = ChannelFixture.createValidChannel();
      channel.addParticipant(userId);

      when(userRepository.findById(userId)).thenReturn(Optional.of(UserFixture.createValidUser()));
      when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
      when(binaryContentRepository.findById(attachmentId1)).thenReturn(
          Optional.of(BinaryContentFixture.createValidMessageAttachment()));
      when(binaryContentRepository.findById(attachmentId2)).thenReturn(
          Optional.of(BinaryContentFixture.createValidMessageAttachment()));
      when(messageRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

      Message result = messageService.create(request);

      assertNotNull(result.getId());
      verify(messageRepository).save(any());
      verify(binaryContentRepository, atLeast(1)).save(any());
    }
  }

  @Nested
  @DisplayName("메시지 조회")
  class Read {

    @Test
    @DisplayName("특정 채널의 메시지 목록을 조회한다")
    void shouldFindAllMessagesByChannelId() {
      UUID channelId = UUID.randomUUID();
      List<Message> messages = List.of(
          MessageFixture.createValidMessage(),
          MessageFixture.createValidMessage()
      );

      when(messageRepository.findAllByChannelId(channelId)).thenReturn(messages);

      List<MessageResponse> result = messageService.findAllByChannelId(channelId);

      assertNotNull(result);
      assertEquals(2, result.size());
      verify(messageRepository).findAllByChannelId(channelId);
    }
  }

  @Nested
  @DisplayName("메시지 업데이트")
  class Update {

    @Test
    @DisplayName("메시지 내용을 DTO로 업데이트한다")
    void shouldUpdateMessageContentUsingDto() {
      UUID messageId = UUID.randomUUID();
      String updatedContent = "수정된 메시지";

      Message message = MessageFixture.createValidMessage();
      when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

      MessageUpdateRequest updateRequest = new MessageUpdateRequest(messageId, updatedContent);

      messageService.updateContent(updateRequest);

      assertEquals(updatedContent, message.getContent());
      verify(messageRepository).save(any());
    }
  }

  @Nested
  @DisplayName("메시지 삭제")
  class Delete {

    @Test
    @DisplayName("메시지를 삭제하면 첨부파일도 같이 삭제된다")
    void shouldDeleteMessageAndAttachmentsTogether() {
      UUID messageId = UUID.randomUUID();
      Message message = MessageFixture.createValidMessage();

      when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

      messageService.delete(messageId);

      verify(messageRepository).save(any());
      verify(binaryContentRepository).deleteAllByMessageId(messageId);
    }
  }
}
