package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JcfChannelRepository implements ChannelRepository {
  private final Map<UUID, Channel> channelMap = new HashMap<>();

  @Override
  public Channel save(Channel channel) {
    channelMap.put(channel.getId(), channel);
    return channel;
  }

  @Override
  public Optional<Channel> findById(UUID channelId) {
    return Optional.ofNullable(channelMap.get(channelId));
  }

  @Override
  public List<Channel> findAll() {
    return new ArrayList<>(channelMap.values());
  }

  @Override
  public void update(Channel channel) {
    if (!channelMap.containsKey(channel.getId())) {
      throw new IllegalArgumentException("채널을 찾을 수 없습니다: " + channel.getId());
    }
    channelMap.put(channel.getId(), channel);  // 이미 존재하는 채널을 덮어씌운다
  }

  @Override
  public void delete(UUID channelId) {
    if (!channelMap.containsKey(channelId)) {
      throw new IllegalArgumentException("채널을 찾을 수 없습니다: " + channelId);
    }
    channelMap.remove(channelId);
  }

  @Override
  public void deleteByOwnerId(UUID userId) {
    channelMap.values().removeIf(channel -> channel.getChannelOwner().getId().equals(userId));
  }

  @Override
  public void removeUserFromAllChannels(UUID userId) {
    for (Channel channel : channelMap.values()) {
      channel.getChannelMembers().removeIf(user -> user.getId().equals(userId));
    }
  }
}
