package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entitiy.Channel;

import java.util.UUID;

public interface ChannelRepository {

    public void save(Channel channel);
    public void read();
    public void readById(UUID id);
    public void update(UUID id, Channel channel);
    public void delete(Channel channel);
}
