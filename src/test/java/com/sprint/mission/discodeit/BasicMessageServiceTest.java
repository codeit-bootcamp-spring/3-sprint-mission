package com.sprint.mission.discodeit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class BasicMessageServiceTest {

  private MessageRepository messageRepository;
  private BinaryContentRepository binaryContentRepository;
  private UserRepository userRepository;
  private ChannelRepository channelRepository;
  private BasicMessageService messageService;

  @BeforeEach
  void setUp() {
    messageRepository = mock(MessageRepository.class);
    binaryContentRepository = mock(BinaryContentRepository.class);
    userRepository = mock(UserRepository.class);
    channelRepository = mock(ChannelRepository.class);
    messageService = new BasicMessageService(messageRepository, binaryContentRepository,
        userRepository, channelRepository);
  }

  @Test
  void create_shouldSaveMessageWithAttachments() {
    UUID channelId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    String content = "Hello World";

    when(channelRepository.existsById(channelId)).thenReturn(true);
    when(userRepository.existsById(userId)).thenReturn(true);

    byte[] data = "test".getBytes();
    MultipartFile multipartFile = new MockMultipartFile(
        "file",
        "file.txt",
        "text/plain",
        data
    );

    Message message = new Message(content, channelId, userId, List.of());
    when(messageRepository.save(any())).thenReturn(message);

    MessageCreateRequest request = new MessageCreateRequest(content, channelId, userId);

    messageService.create(request, List.of(multipartFile));

    verify(messageRepository).save(any(Message.class));
  }

  @Test
  void create_shouldThrowWhenChannelMissing() {
    UUID channelId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("hi", channelId, userId);

    when(channelRepository.existsById(channelId)).thenReturn(false);

    assertThrows(NoSuchElementException.class, () -> messageService.create(request, List.of()));
  }

  @Test
  void find_shouldReturnMessageResponse() {
    UUID id = UUID.randomUUID();
    Message message = mock(Message.class);
    when(messageRepository.findById(id)).thenReturn(Optional.of(message));

    assertNotNull(messageService.find(id));
  }

  @Test
  void find_shouldThrowWhenNotFound() {
    UUID id = UUID.randomUUID();
    when(messageRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> messageService.find(id));
  }

  @Test
  void findAllByChannelId_shouldReturnMessages() {
    UUID channelId = UUID.randomUUID();
    when(messageRepository.findAllByChannelId(channelId)).thenReturn(List.of());

    List<Message> result = messageService.findAllByChannelId(channelId);
    assertNotNull(result);
  }

  @Test
  void update_shouldUpdateMessageContent() {
    UUID messageId = UUID.randomUUID();
    Message message = mock(Message.class);
    when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
    when(messageRepository.save(message)).thenReturn(message);

    MessageUpdateRequest request = new MessageUpdateRequest("updated");
    messageService.update(messageId, request);

    verify(message).update("updated");
    verify(messageRepository).save(message);
  }

  @Test
  void delete_shouldDeleteMessageAndAttachments() {
    UUID messageId = UUID.randomUUID();
    Message message = mock(Message.class);
    when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
    when(message.getAttachmentIds()).thenReturn(List.of(UUID.randomUUID()));

    messageService.delete(messageId);

    verify(messageRepository).deleteById(messageId);
    verify(binaryContentRepository).deleteById(any());
  }
}

