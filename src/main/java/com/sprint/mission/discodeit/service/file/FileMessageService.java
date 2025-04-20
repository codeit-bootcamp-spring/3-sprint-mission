package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {
  private MessageRepository messageRepository = null;
  private final UserService userService;
  private final ChannelService channelService;

  public FileMessageService(MessageRepository messageRepository, UserService userService,
      ChannelService channelService) {
    this.messageRepository = messageRepository; // ← 이게 정답!
    this.userService = userService;
    this.channelService = channelService;
  }


  @Override
  public Message create(UUID userId, UUID channelId, String content) {
    if (userService.findById(userId) == null) {
      throw new IllegalArgumentException("User not found: " + userId);
    }

    if (channelService.findById(channelId) == null) {
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
  public void update(UUID id, String newContent) {
    Message message = messageRepository.findById(id);
    if (message != null) {
      message.updateContent(newContent);
      messageRepository.save(message);
    }
  }

  @Override
  public void delete(UUID id) {
    messageRepository.delete(id);
  }
}
