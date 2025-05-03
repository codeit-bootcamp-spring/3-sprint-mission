package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageDataStore;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {
  private static final String FILE_PATH = "messages.ser";

  private MessageDataStore dataStore;

  public FileMessageRepository() {
    loadData();
  }

  private void loadData() {
    File file = new File(FILE_PATH);
    if (!file.exists()) {
      dataStore = new MessageDataStore(); // 초기화
      return;
    }

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      dataStore = (MessageDataStore) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new IllegalStateException("Message 데이터 로딩 실패", e);
    }
  }

  private void saveData() {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
      oos.writeObject(dataStore);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Message save(Message message) {
    dataStore.getMessageMap().put(message.getId(), message);

    // 채널 인덱스
    dataStore.getChannelMessagesMap()
        .computeIfAbsent(message.getChannelId(), k -> new ArrayList<>())
        .add(message);

    // 유저 인덱스
    dataStore.getUserMessagesMap()
        .computeIfAbsent(message.getSenderId(), k -> new ArrayList<>())
        .add(message);

    saveData();
    return message;
  }

  @Override
  public Optional<Message> findById(UUID messageId) {
    return Optional.ofNullable(dataStore.getMessageMap().get(messageId));
  }

  @Override
  public void delete(UUID messageId) {
    Message message = dataStore.getMessageMap().remove(messageId);
    if (message != null) {
      dataStore.getChannelMessagesMap()
          .getOrDefault(message.getChannelId(), new ArrayList<>())
          .remove(message);
      dataStore.getUserMessagesMap()
          .getOrDefault(message.getSenderId(), new ArrayList<>())
          .remove(message);
      saveData();
    }
  }

  @Override
  public List<Message> findByChannelId(UUID channelId) {
    return new ArrayList<>(dataStore.getChannelMessagesMap()
        .getOrDefault(channelId, Collections.emptyList()));
  }

  @Override
  public List<Message> findBySenderId(UUID senderId) {
    return new ArrayList<>(dataStore.getUserMessagesMap()
        .getOrDefault(senderId, Collections.emptyList()));
  }

  /*
  세 개의 Map을 각각 직렬화하는 대신,    (dataStore 단일 객체만 .ser 파일로 직렬화)
  하나의 Wrapper 객체(MessageDataStore)로 묶어 통합 직렬화하면 구조 변경 시 대응이 가능하다.
   */
}
