package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFChannelRepository implements ChannelRepository {

  private final Map<UUID, Channel> channels = new HashMap<>();

  @Override
  public void insert(Channel channel) {
    if (channels.containsKey(channel.getId())) {
      throw new IllegalArgumentException("이미 존재하는 채널입니다. [ID: " + channel.getId() + "]");
    }
    channels.put(channel.getId(), channel);
  }

  @Override
  public Channel save(Channel channel) {
    channels.put(channel.getId(), channel);
    return channel;
  }

  @Override
  public Optional<Channel> findById(UUID id) {
    return Optional.ofNullable(channels.get(id));
  }

  @Override
  public List<Channel> findAll() {
    return new ArrayList<>(channels.values());
  }

  @Override
  public void update(Channel channel) {
    if (!channels.containsKey(channel.getId())) {
      throw new IllegalArgumentException("존재하지 않는 채널입니다. [ID: " + channel.getId() + "]");
    }
    channels.put(channel.getId(), channel);
  }

  @Override
  public void delete(UUID id) {
    if (!channels.containsKey(id)) {
      throw new IllegalArgumentException("채널을 찾을 수 없습니다. [ID: " + id + "]");
    }
    channels.remove(id);
  }
}
