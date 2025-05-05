package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    Channel create(Channel channel);

    Channel getById(UUID id);

    List<Channel> getAll();

    Channel update(Channel channel);

    void delete(UUID id);

}
