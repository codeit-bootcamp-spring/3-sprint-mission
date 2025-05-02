package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.storage.FileStorage;
import com.sprint.mission.discodeit.repository.storage.FileStorageImpl;
import com.sprint.mission.discodeit.repository.storage.IndexManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserRepository implements UserRepository {

  private static final String DEFAULT_FILE_PATH = "data/users.ser";
  private static final String DEFAULT_INDEX_PATH = "data/users.ser.idx";

  private final FileStorage fileStorage;
  private final IndexManager indexManager;

  private FileUserRepository() {
    try {
      this.fileStorage = new FileStorageImpl(DEFAULT_FILE_PATH);
      this.indexManager = new IndexManager(DEFAULT_INDEX_PATH);
    } catch (Exception e) {
      throw new RuntimeException("FileUserRepository 초기화 실패: " + e.getMessage(), e);
    }
  }

  private FileUserRepository(String filePath) {
    this.fileStorage = new FileStorageImpl(filePath);
    this.indexManager = new IndexManager(filePath + ".idx");
  }

  public static FileUserRepository from(String filePath) {
    return new FileUserRepository(filePath);
  }

  public static FileUserRepository createDefault() {
    return new FileUserRepository();
  }

  @Override
  public Optional<User> findById(UUID id) {
    Long position = indexManager.getPosition(id);
    if (position == null) {
      return Optional.empty();
    }
    return Optional.ofNullable((User) fileStorage.readObject(position));
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return findAll().stream()
        .filter(user -> user.getEmail().equals(email))
        .findFirst();
  }

  @Override
  public Optional<User> findByName(String name) {
    return findAll().stream()
        .filter(user -> user.getName().equals(name)).findFirst();
  }

  @Override
  public Optional<User> findByNameWithPassword(String name, String password) {
    return findAll().stream()
        .filter(user -> user.getName().equals(name) && user.getPassword().equals(password))
        .findFirst();
  }

  @Override
  public List<User> findAll() {
    return fileStorage.readAll().stream()
        .map(obj -> (User) obj)
        .toList();
  }

  @Override
  public User save(User user) {
    Long existingPosition = indexManager.getPosition(user.getId());
    if (existingPosition != null) {
      // 기존 사용자 정보 업데이트
      fileStorage.updateObject(existingPosition, user);
    } else {
      // 새로운 사용자 저장
      long newPosition = fileStorage.saveObject(user);
      indexManager.addEntry(user.getId(), newPosition);
      indexManager.saveIndex();
    }
    return user;
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