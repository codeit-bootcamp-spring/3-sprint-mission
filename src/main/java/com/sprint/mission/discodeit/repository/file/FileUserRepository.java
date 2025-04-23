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
import java.util.UUID;
import org.springframework.stereotype.Repository;


@Repository
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


  private Map<UUID, User> loadFromFile() {
    File file = new File(filePath);
    if (!file.exists()) {
      return new HashMap<>();
    }

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      return (Map<UUID, User>) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      return new HashMap<>();
    }
  }
}
