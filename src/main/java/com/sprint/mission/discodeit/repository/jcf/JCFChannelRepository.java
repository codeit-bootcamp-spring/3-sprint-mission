package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {

  private final Map<UUID, Channel> channels = new HashMap<>();

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
  public void deleteById(UUID id) {
    channels.remove(id);
  }
}
