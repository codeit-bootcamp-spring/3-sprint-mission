package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;


  @Override
  public Message create(UUID userId, UUID channelId, String content) {
    // Check if user and channel exist
    if (userRepository.findById(userId) == null) {
      throw new IllegalArgumentException("User not found: " + userId);
    }
    if (channelRepository.findById(channelId) == null) {
      throw new IllegalArgumentException("Channel not found: " + channelId);
    }

    Message message = new Message(userId, channelId, content);
    messageRepository.save(message);
    return message;
  }

  @Override
  public Message findById(UUID id) {
    return messageRepository.findById(id);
  }

  @Override
  public List<Message> findAll() {
    return messageRepository.findAll();
  }

  @Override
  public Message update(UUID id, String newContent) {
    Message message = messageRepository.findById(id);
    if (message != null) {
      message.updateContent(newContent);
      messageRepository.save(message);
    }
    return message;
  }

  @Override
  public Message delete(UUID id) {
    Message message = messageRepository.findById(id);
    if (message != null) {
      messageRepository.delete(id);
    }
    return message;
  }
}