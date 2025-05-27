package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageDataStore;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileMessageRepository implements MessageRepository {

  private final String filePath;
  private MessageDataStore dataStore;

  public FileMessageRepository(
      @Value("${discodeit.repository.file-directory}") String fileDirectory
  ) {
    this.filePath = Paths.get(System.getProperty("user.dir"), fileDirectory, "messages.ser")
        .toString();
    loadData(); // 데이터를 로드
  }

  private void loadData() {
    File file = new File(filePath);
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
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
      oos.writeObject(dataStore);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public synchronized Message save(Message message) {
    dataStore.getMessageMap().put(message.getId(), message);

    // 채널 인덱스
    dataStore.getChannelMessagesMap()
        .computeIfAbsent(message.getChannelId(), k -> new ArrayList<>())
        .add(message);

    // 유저 인덱스
    dataStore.getUserMessagesMap()
        .computeIfAbsent(message.getAuthorId(), k -> new ArrayList<>())
        .add(message);

    saveData();
    return message;
  }

  @Override
  public synchronized Optional<Message> findById(UUID messageId) {
    return Optional.ofNullable(dataStore.getMessageMap().get(messageId));
  }

  @Override
  public synchronized void delete(UUID messageId) {
    Message message = dataStore.getMessageMap().remove(messageId);
    if (message != null) {
      dataStore.getChannelMessagesMap()
          .getOrDefault(message.getChannelId(), new ArrayList<>())
          .remove(message);
      dataStore.getUserMessagesMap()
          .getOrDefault(message.getAuthorId(), new ArrayList<>())
          .remove(message);
      saveData();
    }
  }

  @Override
  public synchronized void deleteAll(List<Message> messages) {
    for (Message message : messages) {
      delete(message.getId());
    }
  }

  @Override
  public synchronized List<Message> findByChannelId(UUID channelId) {
    return new ArrayList<>(dataStore.getChannelMessagesMap()
        .getOrDefault(channelId, Collections.emptyList()));
  }

  @Override
  public synchronized List<Message> findBySenderId(UUID senderId) {
    return new ArrayList<>(dataStore.getUserMessagesMap()
        .getOrDefault(senderId, Collections.emptyList()));
  }

  /*
  세 개의 Map을 각각 직렬화하는 대신,    (dataStore 단일 객체만 .ser 파일로 직렬화)
  하나의 Wrapper 객체(MessageDataStore)로 묶어 통합 직렬화하면 구조 변경 시 대응이 가능하다.

  모든 공유 자원 접근 메서드에 synchronized 사용 중
  lock 분할 제어가 필요하지 않음 -> ReentrantLock을 사용하지 않음
  ReentrantLock으로 전환? -> 성능 병목이 발생하거나, 데드락이나 기아 상태가 의심될 때
   */
}
