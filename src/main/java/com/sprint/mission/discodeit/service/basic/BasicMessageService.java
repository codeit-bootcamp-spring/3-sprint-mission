package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public Message create(MessageCreateRequest request) {
    Message message = new Message(
        request.getUserId(),
        request.getChannelId(),
        request.getContent()
    );
    messageRepository.save(message);

    if (request.getAttachments() != null) {
      for (byte[] file : request.getAttachments()) {
        BinaryContent content = new BinaryContent(
            request.getUserId(),
            message.getId(),
            file
        );
        binaryContentRepository.save(content);
      }
    }

    return message;
  }

  @Override
  public Message findById(UUID id) {
    return messageRepository.findById(id);
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    return messageRepository.findAll().stream()
        .filter(m -> m.getChannelId().equals(channelId))
        .collect(Collectors.toList());
  }

  @Override
  public Message update(MessageUpdateRequest request) {
    Message message = messageRepository.findById(request.getMessageId());
    if (message != null) {
      message.update(request.getContent());
      messageRepository.save(message);
    }
    return message;
  }

  @Override
  public Message delete(UUID id) {
    Message message = messageRepository.findById(id);
    if (message != null) {
      binaryContentRepository.deleteByMessageId(id);
      messageRepository.delete(id);
    }
    return message;
  }
}
