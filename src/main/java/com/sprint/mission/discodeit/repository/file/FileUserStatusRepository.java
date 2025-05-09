package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.storage.FileStorage;
import com.sprint.mission.discodeit.repository.storage.FileStorageImpl;
import java.util.ArrayList;
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
public class FileUserStatusRepository implements UserStatusRepository {

  private final FileStorage fileStorage;

  public FileUserStatusRepository(
      @Value("${discodeit.repository.file-directory.folder:data}${discodeit.repository.file-directory.user-status:/user-status}") String fileDirectory) {
    this.fileStorage = new FileStorageImpl(fileDirectory);
  }

  @Override
  public void insert(UserStatus userStatus) {
    Optional<UserStatus> existing = findById(userStatus.getId());
    if (existing.isPresent()) {
      throw new IllegalArgumentException("이미 존재하는 UserStatus입니다. [ID: " + userStatus.getId() + "]");
    }
    fileStorage.saveObject(userStatus.getId(), userStatus);
  }

  @Override
  public Optional<UserStatus> findById(UUID id) {
    try {
      return Optional.of((UserStatus) fileStorage.readObject(id));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<UserStatus> findByUserId(UUID userId) {
    return findAll().stream()
        .filter(status -> status.getUserId().equals(userId))
        .findFirst();
  }

  @Override
  public List<UserStatus> findAll() {
    List<Object> allObjects = fileStorage.readAll();
    List<UserStatus> result = new ArrayList<>();
    for (Object obj : allObjects) {
      if (obj instanceof UserStatus) {
        result.add((UserStatus) obj);
      }
    }
    return result;
  }

  @Override
  public UserStatus save(UserStatus userStatus) {
    Optional<UserStatus> existing = findById(userStatus.getId());
    if (existing.isPresent()) {
      fileStorage.updateObject(userStatus.getId(), userStatus);
    } else {
      fileStorage.saveObject(userStatus.getId(), userStatus);
    }
    return userStatus;
  }

  @Override
  public void update(UserStatus userStatus) {
    Optional<UserStatus> existing = findById(userStatus.getId());
    if (existing.isEmpty()) {
      throw new IllegalArgumentException("존재하지 않는 UserStatus입니다. [ID: " + userStatus.getId() + "]");
    }
    fileStorage.updateObject(userStatus.getId(), userStatus);
  }

  @Override
  public void delete(UUID id) {
    fileStorage.deleteObject(id);
  }
}
