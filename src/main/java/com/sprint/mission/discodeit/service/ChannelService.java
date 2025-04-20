package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(String name);
    Channel getChannel(UUID id);
    List<Channel> getAllChannels();
    Channel getChannelByName(String name);
    void updateChannel(UUID id, String name);
    void joinChannel(UUID userId, UUID channelId);
    void leaveChannel(UUID userId, UUID channelId);
    void deleteChannel(UUID id);
    boolean existsById(UUID id); // JavaApplicationd에서 사용됨
}
