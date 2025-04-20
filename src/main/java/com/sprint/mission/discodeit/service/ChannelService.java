package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(Channel channel);
    Optional<Channel> getChannel(UUID channelId);
    List<Channel> getAllChannels();
    void updateChannel(Channel channel);
    void deleteChannel(UUID channelId);
}