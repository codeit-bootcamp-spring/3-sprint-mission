package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entitiy.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {

    public Channel save(Channel channel);
    public List<Channel> read();
    public Optional<Channel> readById(UUID id);
    public void update(UUID id, Channel channel);
    public void delete(UUID channelId);
}
