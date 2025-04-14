package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.Map;
import java.util.UUID;

public interface ChannelRepository {
    void save(Channel channel);

    Map<UUID, Channel> readChannels();

    Channel readChannel(UUID id);

    void deleteChannel(UUID id);
}
