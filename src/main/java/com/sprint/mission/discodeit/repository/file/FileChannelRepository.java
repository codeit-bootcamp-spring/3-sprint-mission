package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.storage.FileStorage;
import com.sprint.mission.discodeit.repository.storage.FileStorageImpl;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file", matchIfMissing = true)
public class FileChannelRepository implements ChannelRepository {

  private final FileStorage<Channel> fileStorage;

  public FileChannelRepository(
      @Value("${discodeit.repository.file-directory.folder:data}${discodeit.repository.file-directory.channel:/channel}") String fileDirectory) {
    this.fileStorage = new FileStorageImpl<>(fileDirectory);
  }

  @Override
  public void insert(Channel channel) {
    Optional<Channel> existing = findById(channel.getId());
    if (existing.isPresent()) {
      throw new IllegalArgumentException("이미 존재하는 채널입니다. [ID: " + channel.getId() + "]");
    }
    fileStorage.saveObject(channel.getId(), channel);
  }

  @Override
  public Channel save(Channel channel) {
    Optional<Channel> existing = findById(channel.getId());
    if (existing.isPresent()) {
      fileStorage.updateObject(channel.getId(), channel);
    } else {
      fileStorage.saveObject(channel.getId(), channel);
    }
    return channel;
  }

  @Override
  public Optional<Channel> findById(UUID id) {
    try {
      return Optional.of((Channel) fileStorage.readObject(id));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public List<Channel> findAll() {
    return fileStorage.readAll().stream()
        .map(obj -> (Channel) obj)
        .toList();
  }

  @Override
  public void update(Channel channel) {
    Optional<Channel> existing = findById(channel.getId());
    if (existing.isEmpty()) {
      throw new IllegalArgumentException("존재하지 않는 채널입니다. [ID: " + channel.getId() + "]");
    }
    fileStorage.updateObject(channel.getId(), channel);
  }

  @Override
  public void delete(UUID id) {
    try {
      fileStorage.deleteObject(id);
    } catch (Exception e) {
      throw new IllegalArgumentException("채널을 삭제하는 도중 오류가 발생했습니다. [ID: " + id + "]", e);
    }
  }
}
