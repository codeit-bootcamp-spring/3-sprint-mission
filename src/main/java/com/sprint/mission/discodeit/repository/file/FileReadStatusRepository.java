package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.common.exception.FileException;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.storage.FileStorage;
import com.sprint.mission.discodeit.repository.storage.FileStorageImpl;
import com.sprint.mission.discodeit.repository.storage.IndexManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileReadStatusRepository implements ReadStatusRepository {

  private static final String DEFAULT_FILE_PATH = "data/read-status/read-status.ser";
  private static final String DEFAULT_INDEX_PATH = "data/read-status/read-status.ser.idx";
  private static final String DEFAULT_USER_CHANNEL_INDEX_PATH = "data/read-status/read-status.ser.ucid.idx";
  private static final String DEFAULT_USER_INDEX_PATH = "data/read-status/read-status.ser.uid.idx";
  private static final String DEFAULT_CHANNEL_INDEX_PATH = "data/read-status/read-status.ser.cid.idx";

  private final FileStorage fileStorage;
  private final IndexManager indexManager;
  private final IndexManager userChannelIndexManager;
  private final IndexManager userIndexManager;
  private final IndexManager channelIndexManager;

  // private 생성자
  private FileReadStatusRepository() {
    this(DEFAULT_FILE_PATH);
  }

  // 커스텀 경로 생성자
  private FileReadStatusRepository(String filePath) {
    try {
      this.fileStorage = new FileStorageImpl(filePath);
      this.indexManager = new IndexManager(resolveIndexPath(filePath, ".idx"));
      this.userChannelIndexManager = new IndexManager(resolveIndexPath(filePath, ".ucid.idx"));
      this.userIndexManager = new IndexManager(resolveIndexPath(filePath, ".uid.idx"));
      this.channelIndexManager = new IndexManager(resolveIndexPath(filePath, ".cid.idx"));
    } catch (Exception e) {
      throw new RuntimeException("FileReadStatusRepository 초기화 실패", e);
    }
  }

  // 팩토리 메서드: 커스텀 경로
  public static FileReadStatusRepository from(String filePath) {
    return new FileReadStatusRepository(filePath);
  }

  // 팩토리 메서드: 기본 경로
  public static FileReadStatusRepository createDefault() {
    return new FileReadStatusRepository();
  }

  // 인덱스 파일 경로 생성 헬퍼
  private String resolveIndexPath(String basePath, String suffix) {
    return basePath.endsWith(".ser") ?
        basePath.replace(".ser", suffix) :
        basePath + suffix;
  }

  @Override
  public Optional<ReadStatus> findById(UUID id) {
    Long position = indexManager.getPosition(id);
    return position != null ?
        Optional.ofNullable((ReadStatus) fileStorage.readObject(position)) :
        Optional.empty();
  }

  @Override
  public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
    String compositeKey = createCompositeKey(userId, channelId);
    Long position = userChannelIndexManager.getPosition(UUID.fromString(compositeKey));
    return position != null ?
        Optional.ofNullable((ReadStatus) fileStorage.readObject(position)) :
        Optional.empty();
  }

  @Override
  public List<ReadStatus> findByUserId(UUID userId) {
    return findMultipleByIndex(userId.toString(), userIndexManager);
  }

  @Override
  public List<ReadStatus> findByChannelId(UUID channelId) {
    return findMultipleByIndex(channelId.toString(), channelIndexManager);
  }

  @Override
  public List<ReadStatus> findAll() {
    return fileStorage.readAll().stream()
        .filter(obj -> obj instanceof ReadStatus)
        .map(obj -> (ReadStatus) obj)
        .toList();
  }

  @Override
  public ReadStatus save(ReadStatus readStatus) {
    try {
      Long existingPosition = indexManager.getPosition(readStatus.getId());
      long storagePosition;

      if (existingPosition != null) {
        fileStorage.updateObject(existingPosition, readStatus);
        storagePosition = existingPosition;
      } else {
        storagePosition = fileStorage.saveObject(readStatus);
        indexManager.addEntry(readStatus.getId(), storagePosition);
      }

      updateAllIndexes(readStatus, storagePosition);
      saveAllIndexes();

      return readStatus;
    } catch (Exception e) {
      throw new RuntimeException("ReadStatus 저장 실패", e);
    }
  }

  @Override
  public void delete(UUID id) {
    try {
      Long position = indexManager.getPosition(id);
      if (position != null) {
        ReadStatus toDelete = (ReadStatus) fileStorage.readObject(position);
        if (toDelete != null) {
          removeFromAllIndexes(toDelete, position);
        }
        fileStorage.deleteObject(position);
        indexManager.removeEntry(id);
        saveAllIndexes();
      }
    } catch (Exception e) {
      throw new RuntimeException("ReadStatus 삭제 실패", e);
    }
  }

  // Helper Methods
  private List<ReadStatus> findMultipleByIndex(String key, IndexManager indexManager) {
    return indexManager.getAllIndexEntries().entrySet().stream()
        .filter(entry -> entry.getKey().toString().equals(key))
        .map(entry -> (ReadStatus) fileStorage.readObject(entry.getValue()))
        .toList();
  }

  private void updateAllIndexes(ReadStatus readStatus, long position) throws FileException {
    String compositeKey = createCompositeKey(readStatus.getUserId(), readStatus.getChannelId());
    userChannelIndexManager.addEntry(compositeKey, position);
    userIndexManager.addEntry(readStatus.getUserId().toString(), position);
    channelIndexManager.addEntry(readStatus.getChannelId().toString(), position);
  }

  private void removeFromAllIndexes(ReadStatus readStatus, long position) throws FileException {
    String compositeKey = createCompositeKey(readStatus.getUserId(), readStatus.getChannelId());
    userChannelIndexManager.removeEntry(UUID.fromString(compositeKey));
    userIndexManager.removeEntry(UUID.fromString(readStatus.getUserId().toString()));
    channelIndexManager.removeEntry(UUID.fromString(readStatus.getChannelId().toString()));
  }

  private void saveAllIndexes() throws FileException {
    indexManager.saveIndex();
    userChannelIndexManager.saveIndex();
    userIndexManager.saveIndex();
    channelIndexManager.saveIndex();
  }

  private String createCompositeKey(UUID userId, UUID channelId) {
    return userId + ":" + channelId;
  }
}