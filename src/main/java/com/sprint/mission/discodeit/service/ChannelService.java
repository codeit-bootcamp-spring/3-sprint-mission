package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(Channel channel);
    Channel getChannel(UUID channelId);
    List<Channel> getAllChannels();
    void updateChannel(UUID channelId, String channelName);
    void deleteChannel(UUID channelId);
    void addMessageToChannel(UUID channelId, UUID messageId);
    void addUserToChannel(UUID channelId, UUID userId);
}
