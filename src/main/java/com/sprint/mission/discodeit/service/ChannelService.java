package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

public interface ChannelService {
    Channel addChannel(String channelName, String channelDesc, String createrName);
    void UpdateChannel(Channel channel, String channelName, String channelDescription);
    void deleteChannel(Channel channel);

    void printAllChannels();

    Channel findChannelByName(String name);
}
