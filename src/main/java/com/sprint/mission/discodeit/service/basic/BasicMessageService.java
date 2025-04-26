package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.exception.ChannelException;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  @Override
  public Message createMessage(String content, UUID userId, UUID channelId)
      throws ChannelException {
    userRepository.findById(userId)
        .orElseThrow(() -> ChannelException.notFound(userId));

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> ChannelException.notFound(channelId));

    boolean isNotParticipant = channel.getParticipants().stream()
        .noneMatch(p -> p.getId().equals(userId));

    if (isNotParticipant) {
      throw ChannelException.participantNotFound(userId, channelId);
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
        .filter(m -> m.getDeletedAt() == null)
        .filter(m ->
            (channelId == null || m.getChannelId().equals(channelId)) &&
                (userId == null || m.getUserId().equals(userId)) &&
                (content == null || m.getContent().contains(content)))
        .sorted(Comparator.comparing(Message::getCreatedAt))
        .collect(Collectors.toList());
  }

  @Override
  public List<Message> getChannelMessages(UUID channelId) {
    return messageRepository.findAll().stream()
        .filter(m -> m.getDeletedAt() == null)
        .filter(m -> m.getChannelId().equals(channelId))
        .sorted(Comparator.comparing(Message::getCreatedAt))
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
        .filter(m -> m.getDeletedAt() == null)
        .map(m -> {
          m.delete();
          return messageRepository.save(m);
        });
  }
}