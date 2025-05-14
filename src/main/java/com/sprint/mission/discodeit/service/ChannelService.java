package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.*;

public interface ChannelService {
    // CREATE
    void save(Channel channel);
    // READ
    Optional<Channel> findById(UUID channelId);
    List<Channel> findAll();
    // UPDATE
    Channel update(Channel channel);
    // DELETE
    void deleteById(UUID channelId);
}
