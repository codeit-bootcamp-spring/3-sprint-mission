package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.storage.FileStorage;
import com.sprint.mission.discodeit.repository.storage.FileStorageImpl;
import com.sprint.mission.discodeit.repository.storage.IndexManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class FileChannelRepository implements ChannelRepository {

  private static final String DEFAULT_FILE_PATH = "data/channels.ser";
  private static final String DEFAULT_INDEX_PATH = "data/channels.ser.idx";

  private final FileStorage fileStorage;
  private final IndexManager indexManager;

  private FileChannelRepository() {
    try {
      this.fileStorage = new FileStorageImpl(DEFAULT_FILE_PATH);
      this.indexManager = new IndexManager(DEFAULT_INDEX_PATH);
    } catch (Exception e) {
      throw new RuntimeException("FileChannelRepository 초기화 실패: " + e.getMessage(), e);
    }
  }

  private FileChannelRepository(String filePath) {
    this.fileStorage = new FileStorageImpl(filePath);
    this.indexManager = new IndexManager(filePath + ".idx");
  }

  public static FileChannelRepository from(String filePath) {
    return new FileChannelRepository(filePath);
  }

  public static FileChannelRepository createDefault() {
    return new FileChannelRepository();
  }

  @Override
  public Channel save(Channel channel) {
    Long existingPosition = indexManager.getPosition(channel.getId());
    if (existingPosition != null) {
      fileStorage.updateObject(existingPosition, channel);
    } else {
      long newPosition = fileStorage.saveObject(channel);
      indexManager.addEntry(channel.getId(), newPosition);
      indexManager.saveIndex();
    }
    return channel;
  }

  @Override
  public Optional<Channel> findById(UUID id) {
    Long position = indexManager.getPosition(id);
    if (position == null) {
      return Optional.empty();
    }
    return Optional.ofNullable((Channel) fileStorage.readObject(position));
  }

  @Override
  public List<Channel> findAll() {
    return fileStorage.readAll().stream()
        .map(obj -> (Channel) obj)
        .toList();
  }

  @Override
  public void delete(UUID id) {
    Long position = indexManager.getPosition(id);
    if (position != null) {
      fileStorage.deleteObject(position);
      indexManager.removeEntry(id);
      indexManager.saveIndex();
    }
  }
}