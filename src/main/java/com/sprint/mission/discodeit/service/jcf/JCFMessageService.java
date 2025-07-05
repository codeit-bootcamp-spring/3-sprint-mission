package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService.ChannelNotFoundException;
import com.sprint.mission.discodeit.service.jcf.JCFUserService.UserNotFoundException;
import com.sprint.mission.discodeit.service.jcf.JCFUserService.UserNotParticipantException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {

  private final List<Message> messagesRepository = new ArrayList<>();
  private final UserService userService;
  private final ChannelService channelService;

  public JCFMessageService(UserService userService, ChannelService channelService) {
    this.userService = userService;
    this.channelService = channelService;
  }

  @Override
  public Message createMessage(String content, UUID userId, UUID channelId) {
    User user = userService.getUserById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
    Channel channel = channelService.getChannelById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(channelId));

    boolean isNotParticipant = channel.getParticipants().stream()
        .noneMatch(p -> p.getId().equals(userId));

    if (isNotParticipant) {
      throw new UserNotParticipantException();
    }

    Message message = Message.create(content, userId, channelId);
    messagesRepository.add(message);
    return message;
  }

  @Override
  public Optional<Message> getMessageById(UUID id) {
    return findMessageById(id);
  }

  @Override
  public List<Message> searchMessages(UUID channelId, UUID userId, String content) {
    return messagesRepository.stream()
        .filter(message -> !message.isDeleted())
        .filter(message ->
            (channelId == null || message.getChannelId().equals(channelId)) &&
                (userId == null || message.getUserId().equals(userId)) &&
                (content == null || message.getContent().contains(content))
        )
        .sorted(Comparator.comparingLong(Message::getCreatedAt))
        .collect(Collectors.toList());
  }

  @Override
  public List<Message> getChannelMessages(UUID channelId) {
    return messagesRepository.stream()
        .filter(m -> !m.isDeleted())
        .filter(m -> m.getChannelId().equals(channelId))
        .sorted(Comparator.comparingLong(Message::getCreatedAt))
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Message> updateMessageContent(UUID id, String content) {
    return findMessageById(id)
        .map(message -> {
          message.updateContent(content);
          return message;
        });
  }

  @Override
  public Optional<Message> deleteMessage(UUID id) {
    return findMessageById(id)
        .filter(message -> !message.isDeleted())
        .map(message -> {
          message.delete();
          return message;
        });
  }

  private Optional<Message> findMessageById(UUID id) {
    return messagesRepository.stream()
        .filter(m -> m.getId().equals(id))
        .findFirst();
  }

  public static class MessageNotFoundException extends RuntimeException {

    public MessageNotFoundException(UUID messageId) {
      super("메시지를 찾을 수 없음: " + messageId);
    }
  }
}