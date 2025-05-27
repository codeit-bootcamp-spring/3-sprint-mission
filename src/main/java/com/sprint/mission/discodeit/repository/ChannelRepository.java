package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChannelRepository {
    Channel save(Channel channel);
    Channel findById(UUID channelId);
    List<Channel> findAll();
    boolean isExistName(String name);
    void delete(UUID channelid);
}
