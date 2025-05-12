package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.storage.FileStorage;
import com.sprint.mission.discodeit.repository.storage.FileStorageImpl;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file", matchIfMissing = true)
public class FileMessageRepository implements MessageRepository {

  private final FileStorage fileStorage;

  public FileMessageRepository(
      @Value("${discodeit.repository.file-directory.folder:data}${discodeit.repository.file-directory.message:/message}") String fileDirectory) {
    this.fileStorage = new FileStorageImpl(fileDirectory);
  }

  @Override
  public void insert(Message message) {
    Optional<Message> existing = findById(message.getId());
    if (existing.isPresent()) {
      throw new IllegalArgumentException("이미 존재하는 메시지입니다. [ID: " + message.getId() + "]");
    }
    fileStorage.saveObject(message.getId(), message);
  }

  @Override
  public Optional<Message> findById(UUID id) {
    try {
      return Optional.of((Message) fileStorage.readObject(id));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public List<Message> findAll() {
    return fileStorage.readAll().stream()
        .map(obj -> (Message) obj)
        .toList();
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    return fileStorage.readAll().stream()
        .map(obj -> (Message) obj)
        .filter(message -> message.getChannelId().equals(channelId))
        .toList();
  }

  @Override
  public Message save(Message message) {
    Optional<Message> existing = findById(message.getId());
    if (existing.isPresent()) {
      fileStorage.updateObject(message.getId(), message);
    } else {
      fileStorage.saveObject(message.getId(), message);
    }
    return message;
  }

  @Override
  public void update(Message message) {
    Optional<Message> existing = findById(message.getId());
    if (existing.isEmpty()) {
      throw new IllegalArgumentException("존재하지 않는 메시지입니다. [ID: " + message.getId() + "]");
    }
    fileStorage.updateObject(message.getId(), message);
  }

  @Override
  public void delete(UUID id) {
    try {
      fileStorage.deleteObject(id);
    } catch (Exception e) {
      throw new IllegalArgumentException("메시지 삭제 실패 [ID: " + id + "]", e);
    }
  }
}
