package com.sprint.mission.discodeit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
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
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BasicMessageServiceTest {

  @Mock
  UserRepository userRepository;
  @Mock
  ChannelRepository channelRepository;
  @Mock
  MessageRepository messageRepository;
  @Mock
  BinaryContentRepository binaryContentRepository;

  @InjectMocks
  BasicMessageService messageService;

  @Nested
  class Create {

    @Test
    void 메시지를_생성하면_첨부파일_없이도_등록된다() {
      UUID userId = UUID.randomUUID();
      UUID channelId = UUID.randomUUID();
      String content = "메시지 내용";

      MessageCreateRequest request = new MessageCreateRequest(content, userId, channelId,
          Optional.empty());
      Channel channel = ChannelFixture.createPublic();

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
    void 첨부파일을_메시지에_연결하면_BinaryContent의_messageId가_업데이트된다() {
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
    void 메시지를_생성할_때_첨부파일도_함께_등록된다() {
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

      Channel channel = ChannelFixture.createPublic();

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
  class Read {

    @Test
    void 특정_채널의_메시지_목록을_조회한다() {
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
  class Update {

    @Test
    void 메시지_내용을_DTO로_업데이트한다() {
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
  class Delete {

    @Test
    void 메시지를_삭제하면_첨부파일도_같이_삭제된다() {
      UUID messageId = UUID.randomUUID();
      Message message = MessageFixture.createValidMessage();

      when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

      messageService.delete(messageId);

      verify(messageRepository).save(any());
      verify(binaryContentRepository).deleteAllByMessageId(messageId);
    }
  }
}
