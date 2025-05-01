package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.storage.FileStorage;
import com.sprint.mission.discodeit.repository.storage.FileStorageImpl;
import com.sprint.mission.discodeit.repository.storage.IndexManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class FileMessageRepository implements MessageRepository {

  private static final String DEFAULT_FILE_PATH = "data/messages.ser";
  private static final String DEFAULT_INDEX_PATH = "data/messages.ser.idx";

  private final FileStorage fileStorage;
  private final IndexManager indexManager;

  private FileMessageRepository() {
    try {
      this.fileStorage = new FileStorageImpl(DEFAULT_FILE_PATH);
      this.indexManager = new IndexManager(DEFAULT_INDEX_PATH);
    } catch (Exception e) {
      throw new RuntimeException("FileMessageRepository 초기화 실패: " + e.getMessage(), e);
    }
  }

  private FileMessageRepository(String filePath) {
    this.fileStorage = new FileStorageImpl(filePath);
    this.indexManager = new IndexManager(filePath + ".idx");
  }

  public static FileMessageRepository from(String filePath) {
    return new FileMessageRepository(filePath);
  }

  public static FileMessageRepository createDefault() {
    return new FileMessageRepository();
  }

  @Override
  public Message save(Message message) {
    Long existingPosition = indexManager.getPosition(message.getId());
    if (existingPosition != null) {
      // 기존 메시지 업데이트
      fileStorage.updateObject(existingPosition, message);
    } else {
      // 새로운 메시지 저장
      long newPosition = fileStorage.saveObject(message);
      indexManager.addEntry(message.getId(), newPosition);
      indexManager.saveIndex();
    }
    return message;
  }

  @Override
  public Optional<Message> findById(UUID id) {
    Long position = indexManager.getPosition(id);
    if (position == null) {
      return Optional.empty();
    }
    return Optional.ofNullable((Message) fileStorage.readObject(position));
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
  public void delete(UUID id) {
    Long position = indexManager.getPosition(id);
    if (position != null) {
      fileStorage.deleteObject(position);
      indexManager.removeEntry(id);
      indexManager.saveIndex();
    }
  }
}