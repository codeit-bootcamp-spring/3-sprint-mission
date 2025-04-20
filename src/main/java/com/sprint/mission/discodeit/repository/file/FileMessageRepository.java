package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {
  private static final String FILE_PATH = "messages.ser";

  private Map<UUID, Message> messageMap = new HashMap<>();
  private Map<UUID, List<Message>> channelMessagesMap = new HashMap<>();
  private Map<UUID, List<Message>> userMessagesMap = new HashMap<>();

  public FileMessageRepository() {
    loadData();
  }

  private void loadData() {
    File file = new File(FILE_PATH);
    if (!file.exists()) return;

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      messageMap = (Map<UUID, Message>) ois.readObject();
      channelMessagesMap = (Map<UUID, List<Message>>) ois.readObject();
      userMessagesMap = (Map<UUID, List<Message>>) ois.readObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void saveData() {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
      oos.writeObject(messageMap);
      oos.writeObject(channelMessagesMap);
      oos.writeObject(userMessagesMap);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Message save(Message message) {
    messageMap.put(message.getId(), message);
    channelMessagesMap.computeIfAbsent(message.getChannelId(), k -> new ArrayList<>()).add(message);
    userMessagesMap.computeIfAbsent(message.getSenderId(), k -> new ArrayList<>()).add(message);
    saveData();
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
      channelMessagesMap.getOrDefault(msg.getChannelId(), new ArrayList<>()).remove(msg);
      userMessagesMap.getOrDefault(msg.getSenderId(), new ArrayList<>()).remove(msg);
      saveData();
    }
  }

  @Override
  public List<Message> findByChannelId(UUID channelId) {
    return new ArrayList<>(channelMessagesMap.getOrDefault(channelId, Collections.emptyList()));
  }

  @Override
  public List<Message> findBySenderId(UUID senderId) {
    return new ArrayList<>(userMessagesMap.getOrDefault(senderId, Collections.emptyList()));
  }

  @Override
  public void removeFromChannel(UUID channelId, Message message) {
    channelMessagesMap.getOrDefault(channelId, new ArrayList<>()).remove(message);
    saveData();
  }

  @Override
  public void removeFromUser(UUID userId, Message message) {
    userMessagesMap.getOrDefault(userId, new ArrayList<>()).remove(message);
    saveData();
  }
}