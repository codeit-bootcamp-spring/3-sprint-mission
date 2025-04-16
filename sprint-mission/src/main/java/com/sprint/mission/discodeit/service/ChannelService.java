package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.Map;
import java.util.UUID;

public interface ChannelService {

    Channel createChannel(String channelName);

    Map<UUID, Channel> readChannels();
    Channel readChannel(UUID id);

    Channel addMessageToChannel(UUID channelId, UUID messageId);
    Channel addUserToChannel(UUID channelId, UUID userId);
    Channel updateChannel(UUID id, String channelName);
    Channel deleteChannel(UUID id);
}
