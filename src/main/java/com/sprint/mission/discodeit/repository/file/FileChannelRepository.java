package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
public class FileChannelRepository implements ChannelRepository {
  private final String filePath = "channels.dat";
  private final Map<UUID, Channel> data;

  public FileChannelRepository() {
    this.data = loadFromFile();
  }

  @Override
  public void save(Channel channel) {
    data.put(channel.getId(), channel);
    saveToFile();
  }

  @Override
  public Channel findById(UUID id) {
    return data.get(id);
  }

  @Override
  public List<Channel> findAll() {
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
  private Map<UUID, Channel> loadFromFile() {
    File file = new File(filePath);
    if (!file.exists()) return new HashMap<>();

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      return (Map<UUID, Channel>) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      return new HashMap<>();
    }
  }
}
