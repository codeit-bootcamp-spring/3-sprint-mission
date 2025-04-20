package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository {

    void create(Channel channel);

    Channel findById(UUID id);

    List<Channel> findAll();

    void update(UUID id, String newName, String newDescription);

    void delete(UUID id);

}
