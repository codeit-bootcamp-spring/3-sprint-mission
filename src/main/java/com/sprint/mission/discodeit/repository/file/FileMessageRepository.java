package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
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

public class FileMessageRepository implements MessageRepository {

  private final String FILE_PATH;
  private Map<UUID, Message> messages = new HashMap<>();

  private FileMessageRepository(String filePath) {
    this.FILE_PATH = filePath;
    loadData();
  }

  public static FileMessageRepository from(String filePath) {
    return new FileMessageRepository(filePath);
  }

  public static FileMessageRepository createDefault() {
    return new FileMessageRepository("data/messages.ser");
  }

  @Override
  public Message save(Message message) {
    loadData();
    messages.put(message.getId(), message);
    saveData();
    return message;
  }

  @Override
  public Optional<Message> findById(UUID id) {
    loadData();
    return Optional.ofNullable(messages.get(id));
  }

  @Override
  public List<Message> findAll() {
    loadData();
    return new ArrayList<>(messages.values());
  }

  @Override
  public void deleteById(UUID id) {
    loadData();
    messages.remove(id);
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
        messages = (Map<UUID, Message>) map;
      }
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void saveData() {
    File file = new File(FILE_PATH);
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
      oos.writeObject(messages);
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