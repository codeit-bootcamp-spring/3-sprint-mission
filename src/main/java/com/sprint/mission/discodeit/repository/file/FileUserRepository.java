package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
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
public class FileUserRepository implements UserRepository {

  private final FileStorage<User> fileStorage;

  public FileUserRepository(
      @Value("${discodeit.repository.file-directory.folder:data}${discodeit.repository.file-directory.user:/user}") String fileDirectory) {
    this.fileStorage = new FileStorageImpl<>(fileDirectory);
  }

  @Override
  public void insert(User user) {
    Optional<User> existing = findById(user.getId());
    if (existing.isPresent()) {
      throw new IllegalArgumentException("이미 존재하는 사용자입니다. [ID: " + user.getId() + "]");
    }
    fileStorage.saveObject(user.getId(), user);
  }

  @Override
  public Optional<User> findById(UUID id) {
    try {
      return Optional.of((User) fileStorage.readObject(id));
    } catch (Exception e) {
      return Optional.empty();
    }
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
        .filter(user -> user.getUsername().equals(name))
        .findFirst();
  }

  @Override
  public Optional<User> findByNameAndPassword(String name, String password) {
    return findAll().stream()
        .filter(user -> user.getUsername().equals(name) && user.getPassword().equals(password))
        .findFirst();
  }

  @Override
  public List<User> findAll() {
    List<User> allObjects = fileStorage.readAll();
    List<User> users = new ArrayList<>();
    for (User obj : allObjects) {
      if (obj != null) {
        users.add(obj);
      }
    }
    return users;
  }

  @Override
  public User save(User user) {
    Optional<User> existing = findById(user.getId());
    if (existing.isPresent()) {
      fileStorage.updateObject(user.getId(), user);
    } else {
      fileStorage.saveObject(user.getId(), user);
    }
    return user;
  }

  @Override
  public void update(User user) {
    Optional<User> existing = findById(user.getId());
    if (existing.isEmpty()) {
      throw new IllegalArgumentException("존재하지 않는 사용자입니다. [ID: " + user.getId() + "]");
    }
    fileStorage.updateObject(user.getId(), user);
  }

  @Override
  public void delete(UUID id) {
    fileStorage.deleteObject(id);
  }
}
