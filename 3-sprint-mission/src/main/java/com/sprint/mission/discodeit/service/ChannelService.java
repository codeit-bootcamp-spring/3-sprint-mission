package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    public void create(Channel channel);

    public List<Channel> readById(UUID id);

    public List<Channel> readAll();

    public void update(UUID id, String channelName, String channelDescription,
                boolean isPrivate);

    public void deleteById(UUID id);

}
