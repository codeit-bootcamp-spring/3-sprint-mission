package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entitiy.Channel;
import com.sprint.mission.discodeit.entitiy.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    public void create(Channel channel);
    public void readAll();
    public void readById(UUID id);
    public void update(UUID id, Channel channel);
    public void delete(Channel channel);
}
