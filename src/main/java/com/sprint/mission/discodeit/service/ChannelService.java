package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(Channel channel); // C
    Channel readChannel(UUID id); // R
    List<Channel> readAllChannels();
    void updateChannel(UUID id, String newName); // U
    void deleteChannel(UUID id); // D
}
