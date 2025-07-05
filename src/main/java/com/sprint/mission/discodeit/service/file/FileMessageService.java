package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService.ChannelNotFoundException;
import com.sprint.mission.discodeit.service.file.FileUserService.UserNotFoundException;
import com.sprint.mission.discodeit.service.file.FileUserService.UserNotParticipantException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final UserService userService;
  private final ChannelService channelService;

  public FileMessageService(MessageRepository messageRepository,
      UserService userService,
      ChannelService channelService) {
    this.messageRepository = messageRepository;
    this.userService = userService;
    this.channelService = channelService;
  }

  public static FileMessageService from(UserService userService, ChannelService channelService, String filePath) {
    return new FileMessageService(FileMessageRepository.from(filePath), userService, channelService);
  }

  public static FileMessageService createDefault(UserService userService, ChannelService channelService) {
    return new FileMessageService(FileMessageRepository.createDefault(), userService, channelService);
  }

  @Override
  public Message createMessage(String content, UUID userId, UUID channelId) {
    userService.getUserById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
    Channel channel = channelService.getChannelById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(channelId));

    boolean isNotParticipant = channel.getParticipants().stream()
        .noneMatch(p -> p.getId().equals(userId));

    if (isNotParticipant) {
      throw new UserNotParticipantException();
    }

    Message message = Message.create(content, userId, channelId);
    return messageRepository.save(message);
  }

  @Override
  public Optional<Message> getMessageById(UUID id) {
    return messageRepository.findById(id);
  }

  @Override
  public List<Message> searchMessages(UUID channelId, UUID userId, String content) {
    // 채널/사용자 존재 여부 검증 (필요 시)
    if (channelId != null) {
      channelService.getChannelById(channelId)
          .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + channelId));
    }
    if (userId != null) {
      userService.getUserById(userId)
          .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    return messageRepository.findAll().stream()
        .filter(message -> channelId == null || message.getChannelId().equals(channelId))
        .filter(message -> userId == null || message.getUserId().equals(userId))
        .filter(message -> content == null || message.getContent().contains(content))
        .toList();
  }

  @Override
  public List<Message> getChannelMessages(UUID channelId) {
    return messageRepository.findAll().stream()
        .filter(message -> message.getChannelId().equals(channelId))
        .toList();
  }

  @Override
  public Optional<Message> updateMessageContent(UUID id, String content) {
    return messageRepository.findById(id)
        .map(message -> {
          message.updateContent(content);
          return messageRepository.save(message);
        });
  }

  @Override
  public Optional<Message> deleteMessage(UUID id) {
    Optional<Message> message = messageRepository.findById(id);
    message.ifPresent(m -> messageRepository.deleteById(id));
    return message;
  }
}
