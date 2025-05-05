package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;
import org.springframework.stereotype.Repository;


@Repository
public class FileMessageRepository implements MessageRepository {
  private final String filePath = "messages.dat";
  private final Map<UUID, Message> data;

  public FileMessageRepository() {
    this.data = loadFromFile();
  }

  @Override
  public void save(Message message) {
    data.put(message.getId(), message);
    saveToFile();
  }

  @Override
  public Message findById(UUID id) {
    return data.get(id);
  }

  @Override
  public List<Message> findAll() {
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
  private Map<UUID, Message> loadFromFile() {
    File file = new File(filePath);
    if (!file.exists()) return new HashMap<>();

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      return (Map<UUID, Message>) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      return new HashMap<>();
    }
  }
}
