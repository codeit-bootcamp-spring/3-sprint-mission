package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
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
import org.springframework.stereotype.Repository;

@Repository
public class FileChannelRepository implements ChannelRepository {

  private static final String FILE_PATH = "data/channels.ser";
  private final String filePath;
  private Map<UUID, Channel> channels = new HashMap<>();

  private FileChannelRepository() {
    this.filePath = FILE_PATH;
    loadData();
  }

  private FileChannelRepository(String filePath) {
    this.filePath = filePath;
    loadData();
  }

  public static FileChannelRepository from(String filePath) {
    return new FileChannelRepository(filePath);
  }

  public static FileChannelRepository createDefault() {
    return new FileChannelRepository();
  }

  @Override
  public Channel save(Channel channel) {
    loadData();
    channels.put(channel.getId(), channel);
    saveData();
    return channel;
  }

  @Override
  public Optional<Channel> findById(UUID id) {
    loadData();
    return Optional.ofNullable(channels.get(id));
  }

  @Override
  public List<Channel> findAll() {
    loadData();
    return new ArrayList<>(channels.values());
  }

  @Override
  public void deleteById(UUID id) {
    loadData();
    channels.remove(id);
    saveData();
  }

  @SuppressWarnings("unchecked")
  private void loadData() {
    File file = new File(filePath);
    if (!file.exists() || file.length() == 0) {
      createDataFile();
      return;
    }

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      Object obj = ois.readObject();
      if (obj instanceof Map<?, ?> map) {
        channels = (Map<UUID, Channel>) map;
      }
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void saveData() {
    File file = new File(filePath);
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
      oos.writeObject(channels);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void createDataFile() {
    File file = new File(filePath);
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