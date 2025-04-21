package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {
  private final String filePath = "users.dat";
  private final Map<UUID, User> data;

  public FileUserRepository() {
    this.data = loadFromFile();
  }

  @Override
  public void save(User user) {
    data.put(user.getId(), user);
    saveToFile();
  }

  @Override
  public User findById(UUID id) {
    return data.get(id);
  }

  @Override
  public List<User> findAll() {
    return new ArrayList<>(data.values());
  }

  @Override
  public void delete(UUID id) {
    data.remove(id);
    saveToFile();
  }

  private void saveToFile() {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
      oos.writeObject(data);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private Map<UUID, User> loadFromFile() {
    File file = new File(filePath);
    if (!file.exists()) return new HashMap<>();

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      return (Map<UUID, User>) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      return new HashMap<>();
    }
  }
}
