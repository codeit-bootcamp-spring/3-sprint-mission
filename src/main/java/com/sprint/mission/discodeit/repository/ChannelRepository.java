package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {
    Channel save(Channel channel); // 저장 로직
    Optional<Channel> findById(UUID channelId); // 저장 로직
    List<Channel> findAll(); // 저장 로직
    void deleteById(UUID channelId); // 저장 로직
}
