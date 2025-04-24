package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository {
    void save(Channel channel);
    Channel loadByName(String name);
    Channel loadById(UUID id);
    List<Channel> loadAll();
    void update(UUID id, String name);
    void join(UUID userId, UUID channelId);
    void leave(UUID userId, UUID channelId);
    void delete(UUID id);
}
