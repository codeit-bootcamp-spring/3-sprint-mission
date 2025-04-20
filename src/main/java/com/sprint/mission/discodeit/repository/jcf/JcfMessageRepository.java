package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JcfMessageRepository implements MessageRepository {

  private final Map<UUID, Message> messageMap = new HashMap<>();
  private final Map<UUID, List<Message>> channelMessagesMap = new HashMap<>();
  private final Map<UUID, List<Message>> userMessagesMap = new HashMap<>();

  @Override
  public Message save(Message message) {
    messageMap.put(message.getId(), message);
    channelMessagesMap.computeIfAbsent(message.getChannelId(), k -> new ArrayList<>()).add(message);
    userMessagesMap.computeIfAbsent(message.getSenderId(), k -> new ArrayList<>()).add(message);
    return message;
  }

  @Override
  public Optional<Message> findById(UUID messageId) {
    return Optional.ofNullable(messageMap.get(messageId));
  }

  @Override
  public void delete(UUID messageId) {
    Message msg = messageMap.remove(messageId);
    if (msg != null) {
      removeFromChannel(msg.getChannelId(), msg);
      removeFromUser(msg.getSenderId(), msg);
    }
  }

  @Override
  public List<Message> findByChannelId(UUID channelId) {
    return channelMessagesMap.getOrDefault(channelId, Collections.emptyList());
  }

  @Override
  public List<Message> findBySenderId(UUID senderId) {
    return userMessagesMap.getOrDefault(senderId, Collections.emptyList());
  }

  @Override
  public void removeFromChannel(UUID channelId, Message message) {
    channelMessagesMap.getOrDefault(channelId, new ArrayList<>()).remove(message);
  }

  @Override
  public void removeFromUser(UUID userId, Message message) {
    userMessagesMap.getOrDefault(userId, new ArrayList<>()).remove(message);
  }
}
