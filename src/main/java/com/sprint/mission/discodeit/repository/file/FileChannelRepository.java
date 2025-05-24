package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.util.FileSaveManager;
import jakarta.annotation.PostConstruct;
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
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileChannelRepository implements ChannelRepository {

  @Value("${discodeit.repository.file-directory}")
  private String FILE_DIRECTORY;
  private final String FILENAME = "channelRepo.ser";
  private final Map<UUID, Channel> data = new ConcurrentHashMap<>();

  @PostConstruct
  public void init() {
    Map<UUID, Channel> loaded = FileSaveManager.loadFromFile(getFile());
    if (loaded != null) {
      data.putAll(loaded);
    }
  }

  private File getFile() {
    return new File(FILE_DIRECTORY, FILENAME);
  }

  @Override
  public Channel save(Channel channel) {
    data.put(channel.getId(), channel);

    FileSaveManager.saveToFile(getFile(), data);

    return channel;
  }

  @Override
  public Optional<Channel> findById(UUID channelId) {
    Optional<Channel> foundChannel = data.entrySet().stream()
        .filter(entry -> entry.getKey().equals(channelId))
        .map(Map.Entry::getValue)
        .findFirst();

    return foundChannel;
  }

  @Override
  public List<Channel> findByPrivateChannelUserId(UUID userId) {
    return data.values().stream()
        .filter(channel -> channel.getParticipantIds().contains(userId) && channel.getType()
            .equals(ChannelType.PRIVATE))
        .toList();
  }

  @Override
  public List<Channel> findByNameContaining(String name) {
    return data.values().stream()
        .filter(channel -> channel.getType().equals(ChannelType.PUBLIC) && channel.getName()
            .contains(name))
        .toList();
  }

  @Override
  public List<Channel> findAll() {
    return new ArrayList<>(data.values());
  }

  @Override
  public void deleteById(UUID channelId) {
    data.remove(channelId);
    // Channel 삭제 후 파일에 덮어쓰기
    FileSaveManager.saveToFile(getFile(), data);
  }
}
