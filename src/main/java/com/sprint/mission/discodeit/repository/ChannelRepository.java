package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {

  public Channel save(Channel channel);

  public Optional<Channel> find(UUID id);

  public List<Channel> findAll();

  boolean existsById(UUID id);

  void deleteById(UUID id);
}
