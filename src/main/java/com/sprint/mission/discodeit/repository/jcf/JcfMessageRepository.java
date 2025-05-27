package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JcfMessageRepository implements MessageRepository {

  private final Map<UUID, Message> messageMap = new ConcurrentHashMap<>();
  private final Map<UUID, List<Message>> channelMessagesMap = new ConcurrentHashMap<>();
  private final Map<UUID, List<Message>> userMessagesMap = new ConcurrentHashMap<>();

  @Override
  public Message save(Message message) {
    messageMap.put(message.getId(), message);
    channelMessagesMap.computeIfAbsent(message.getChannelId(), k -> new ArrayList<>()).add(message);
    userMessagesMap.computeIfAbsent(message.getAuthorId(), k -> new ArrayList<>()).add(message);
    return message;
  }

  @Override
  public Optional<Message> findById(UUID messageId) {
    return Optional.ofNullable(messageMap.get(messageId));
  }

  @Override
  public void delete(UUID messageId) {
    messageMap.remove(messageId);
  }

  @Override
  public void deleteAll(List<Message> messages) {

  }

  @Override
  public List<Message> findByChannelId(UUID channelId) {
    return channelMessagesMap.getOrDefault(channelId, Collections.emptyList());
  }

  @Override
  public List<Message> findBySenderId(UUID senderId) {
    return userMessagesMap.getOrDefault(senderId, Collections.emptyList());
  }

//  @Override
//  public void removeFromChannel(UUID channelId, Message message) {
//    channelMessagesMap.getOrDefault(channelId, new ArrayList<>()).remove(message);
//  }
//
//  @Override
//  public void removeFromUser(UUID userId, Message message) {
//    userMessagesMap.getOrDefault(userId, new ArrayList<>()).remove(message);
//  }
}
