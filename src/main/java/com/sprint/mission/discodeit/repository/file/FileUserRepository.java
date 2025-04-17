package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FileUserRepository implements UserRepository {

  private final String FILE_PATH;
  private Map<UUID, User> users = new HashMap<>();

  private FileUserRepository(String filePath) {
    this.FILE_PATH = filePath;
    loadData();
  }

  public static FileUserRepository from(String filePath) {
    return new FileUserRepository(filePath);
  }

  public static FileUserRepository createDefault() {
    return new FileUserRepository("data/users.ser");
  }

  @Override
  public Optional<User> findById(UUID id) {
    loadData();
    return Optional.ofNullable(users.get(id));
  }

  @Override
  public Optional<User> findByEmail(String email) {
    loadData();
    return users.values().stream()
        .filter(user -> user.getEmail().equals(email))
        .findFirst();
  }

  @Override
  public List<User> findByNameContains(String name) {
    loadData();
    return users.values().stream()
        .filter(user -> user.getName().contains(name))
        .toList();
  }

  @Override
  public List<User> findAll() {
    loadData();
    return new ArrayList<>(users.values());
  }

  @Override
  public User save(User user) {
    loadData();
    users.put(user.getId(), user);
    saveData();
    return user;
  }

  @Override
  public void deleteById(UUID id) {
    loadData();
    users.remove(id);
    saveData();
  }

  @SuppressWarnings("unchecked")
  private void loadData() {
    File file = new File(FILE_PATH);
    if (!file.exists() || file.length() == 0) {
      createDataFile();
      return;
    }

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      Object obj = ois.readObject();
      if (obj instanceof Map<?, ?> map) {
        users = (Map<UUID, User>) map;
      }
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void saveData() {
    File file = new File(FILE_PATH);
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
      oos.writeObject(users);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void createDataFile() {
    File file = new File(FILE_PATH);
    File parentDir = file.getParentFile();
    if (parentDir != null && !parentDir.exists()) {
      parentDir.mkdirs();
    }

    try {
      file.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
