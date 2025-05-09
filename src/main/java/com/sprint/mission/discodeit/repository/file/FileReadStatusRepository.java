package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.storage.FileStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileReadStatusRepository implements ReadStatusRepository {

  private final FileStorage fileStorage;

  private FileReadStatusRepository(FileStorage fileStorage) {
    this.fileStorage = fileStorage;
  }

  public static FileReadStatusRepository create(FileStorage fileStorage) {
    return new FileReadStatusRepository(fileStorage);
  }

  @Override
  public void insert(ReadStatus readStatus) {
    Optional<ReadStatus> existing = findByUserIdAndChannelId(readStatus.getUserId(),
        readStatus.getChannelId());
    if (existing.isPresent()) {
      throw new IllegalArgumentException(
          "이미 존재하는 읽기 상태입니다. [UserID: " + readStatus.getUserId() + ", ChannelID: "
              + readStatus.getChannelId() + "]");
    }
    fileStorage.saveObject(readStatus.getId(), readStatus);
  }

  @Override
  public Optional<ReadStatus> findById(UUID id) {
    try {
      return Optional.of((ReadStatus) fileStorage.readObject(id));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
    return findAll().stream()
        .filter(readStatus -> readStatus.getUserId().equals(userId) && readStatus.getChannelId()
            .equals(channelId))
        .findFirst();
  }

  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return findAll().stream()
        .filter(readStatus -> readStatus.getUserId().equals(userId))
        .toList();
  }

  @Override
  public List<ReadStatus> findAllByChannelId(UUID channelId) {
    return findAll().stream()
        .filter(readStatus -> readStatus.getChannelId().equals(channelId))
        .toList();
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
    Optional<ReadStatus> existing = findById(readStatus.getId());
    if (existing.isPresent()) {
      fileStorage.updateObject(readStatus.getId(), readStatus);
    } else {
      fileStorage.saveObject(readStatus.getId(), readStatus);
    }
    return readStatus;
  }

  @Override
  public void update(ReadStatus readStatus) {
    Optional<ReadStatus> existing = findById(readStatus.getId());
    if (existing.isEmpty()) {
      throw new IllegalArgumentException(
          "존재하지 않는 읽기 상태입니다. [ReadStatusID: " + readStatus.getId() + "]");
    }
    fileStorage.updateObject(readStatus.getId(), readStatus);
  }

  @Override
  public void delete(UUID id) {
    try {
      fileStorage.deleteObject(id);
    } catch (Exception e) {
      throw new IllegalArgumentException("읽기 상태 삭제 실패 [ID: " + id + "]", e);
    }
  }
}
