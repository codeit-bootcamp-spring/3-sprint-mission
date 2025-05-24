package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.util.FileSaveManager;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileMessageRepository implements MessageRepository {

  @Value("${discodeit.repository.file-directory}")
  private String FILE_DIRECTORY;
  private final String FILENAME = "messageRepo.ser";
  private final Map<UUID, Message> data = new ConcurrentHashMap<>();

  @PostConstruct
  public void init() {
    Map<UUID, Message> loaded = FileSaveManager.loadFromFile(getFile());
    if (loaded != null) {
      data.putAll(loaded);
    }
  }

  private File getFile() {
    return new File(FILE_DIRECTORY, FILENAME);
  }

  @Override
  public Message save(Message message) {
    data.put(message.getId(), message);

    FileSaveManager.saveToFile(getFile(), data);

    return message;
  }

  @Override
  public Optional<Message> findById(UUID messageId) {
    Optional<Message> foundMessage = data.entrySet().stream()
        .filter(entry -> entry.getKey().equals(messageId))
        .map(Map.Entry::getValue)
        .findFirst();

    return foundMessage;
  }

  @Override
  public List<Message> findAll() {
    return new ArrayList<>(data.values());
  }

  @Override
  public List<Message> findMessagesByChannelId(UUID channelId) {
    return data.values().stream()
        .filter(message -> message.getChannelId().equals(channelId))
        .toList();
  }

  @Override
  public List<Message> findMessagesByUserId(UUID userId) {
    return data.values().stream()
        .filter(message -> message.getAuthorId().equals(userId))
        .toList();
  }

  @Override
  public List<Message> findMessageByContainingWord(String word) {
    return data.values().stream()
        .filter(message -> message.getContent().contains(word))
        .toList();
  }

  @Override
  public void deleteById(UUID messageId) {
    data.remove(messageId);
    // Message 삭제 후 파일에 덮어쓰기
    FileSaveManager.saveToFile(getFile(), data);
  }
}
