package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {
  private final String filePath = "users.dat";
  private final Map<UUID, User> data = loadFromFile();

  public FileUserService(UserRepository userRepository) {
  }

  @Override
  public User create(String username) {
    User user = new User(username);
    data.put(user.getId(), user);
    saveToFile();
    return user;
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
  public void update(UUID id, String newUsername) {
    User user = data.get(id);
    if (user != null) {
      user.updateUsername(newUsername);
      saveToFile();
    }
  }

  @Override
  public void delete(UUID id) {
    data.remove(id);
    saveToFile();
  }

  private void saveToFile() {
    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
      out.writeObject(data);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private Map<UUID, User> loadFromFile() {
    File file = new File(filePath);
    if (!file.exists()) return new HashMap<>();

    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
      return (Map<UUID, User>) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
