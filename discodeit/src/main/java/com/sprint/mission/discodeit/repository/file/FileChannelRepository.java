package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.util.FileioUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@ConditionalOnProperty(value = "repository.type", havingValue = "file")
@Repository
public class FileChannelRepository implements ChannelRepository {

  private final Map<String, Channel> channelData;
  private final Path path;

  public FileChannelRepository(@Value("${repository.channel-file-path}") Path path) {
    this.path = path;
    if (!Files.exists(this.path)) {
      try {
        Files.createFile(this.path);
        FileioUtil.save(this.path, new HashMap<>());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    FileioUtil.init(this.path);
    this.channelData = FileioUtil.load(this.path, Channel.class);

  }


  @Override
  public Channel save(Channel channel) {
    channelData.put(channel.getId().toString(), channel);
    FileioUtil.save(this.path, channelData);
    return channel;
  }

  @Override
  public Optional<Channel> findById(UUID id) {
    if (!channelData.containsKey(id.toString())) {
      throw new NoSuchElementException("Channel not found with id " + id);
    }
    return Optional.ofNullable(channelData.get(id.toString()));
  }

  @Override
  public List<Channel> findAll() {
    return channelData.values().stream().toList();
  }


  @Override
  public boolean existsById(UUID id) {
    return channelData.containsKey(id.toString());
  }

  @Override
  public void deleteById(UUID id) {
    channelData.remove(id.toString());
    FileioUtil.save(this.path, channelData);
  }
}
