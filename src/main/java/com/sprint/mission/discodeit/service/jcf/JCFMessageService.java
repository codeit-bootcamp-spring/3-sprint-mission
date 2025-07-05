package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService.ChannelNotFoundException;
import com.sprint.mission.discodeit.service.jcf.JCFUserService.UserNotFoundException;
import com.sprint.mission.discodeit.service.jcf.JCFUserService.UserNotParticipantException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final UserService userService;
  private final ChannelService channelService;

  public JCFMessageService(
      MessageRepository messageRepository,
      UserService userService,
      ChannelService channelService
  ) {
    this.messageRepository = messageRepository;
    this.userService = userService;
    this.channelService = channelService;
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
    return messageRepository.findAll().stream()
        .filter(message -> !message.isDeleted())
        .filter(message ->
            (channelId == null || message.getChannelId().equals(channelId)) &&
                (userId == null || message.getUserId().equals(userId)) &&
                (content == null || message.getContent().contains(content)))
        .sorted(Comparator.comparingLong(Message::getCreatedAt))
        .collect(Collectors.toList());
  }

  @Override
  public List<Message> getChannelMessages(UUID channelId) {
    return messageRepository.findAll().stream()
        .filter(m -> !m.isDeleted())
        .filter(m -> m.getChannelId().equals(channelId))
        .sorted(Comparator.comparingLong(Message::getCreatedAt))
        .collect(Collectors.toList());
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
    return messageRepository.findById(id)
        .filter(message -> !message.isDeleted())
        .map(message -> {
          message.delete();
          return messageRepository.save(message);
        });
  }

  public static class MessageNotFoundException extends RuntimeException {

    public MessageNotFoundException(UUID messageId) {
      super("메시지를 찾을 수 없음: " + messageId);
    }
  }
}
