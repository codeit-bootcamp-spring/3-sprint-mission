package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    public void create(Channel channel);

    public List<Channel> readById(String channelName);

    public List<Channel> readAll();

    public void update( String channelName, String ModifiedChannelName, String channelDescription,
                boolean isPrivate);

    public void deleteById(String channelName);

}
