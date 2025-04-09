package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;

public interface ChannelService {
    Channel addChannel(String channelName, String channelDesc, User user);
    void UpdateChannel(String channelName, String channelDescription);
    void deleteChannel(Channel channel);

    void printAllChannels();
    List<Channel> getAllChannels();

    Channel findChannelByName(String name);
}
