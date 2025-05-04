package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.storage.FileStorage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserRepository implements UserRepository {

  private final FileStorage fileStorage;

  private FileUserRepository(FileStorage fileStorage) {
    this.fileStorage = fileStorage;
  }

  public static FileUserRepository create(FileStorage fileStorage) {
    return new FileUserRepository(fileStorage);
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
        .filter(user -> user.getName().equals(name))
        .findFirst();
  }

  @Override
  public Optional<User> findByNameWithPassword(String name, String password) {
    return findAll().stream()
        .filter(user -> user.getName().equals(name) && user.getPassword().equals(password))
        .findFirst();
  }

  @Override
  public List<User> findAll() {
    List<Object> allObjects = fileStorage.readAll();
    List<User> users = new ArrayList<>();
    for (Object obj : allObjects) {
      if (obj instanceof User) {
        users.add((User) obj);
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
