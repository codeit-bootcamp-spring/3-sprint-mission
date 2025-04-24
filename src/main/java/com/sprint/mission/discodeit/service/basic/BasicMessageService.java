package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService.UserNotFoundException;
import com.sprint.mission.discodeit.service.basic.BasicUserService.UserNotParticipantException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  public BasicMessageService(
      MessageRepository messageRepository,
      UserRepository userRepository,
      ChannelRepository channelRepository
  ) {
    this.messageRepository = messageRepository;
    this.userRepository = userRepository;
    this.channelRepository = channelRepository;
  }

  @Override
  public Message createMessage(String content, UUID userId, UUID channelId)
      throws ChannelException {
    userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> ChannelException.notFound(channelId));

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