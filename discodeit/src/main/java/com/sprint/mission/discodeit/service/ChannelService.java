package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelDTO;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(CreatePublicChannelRequest createPublicChannelRequest);
    Channel create(CreatePrivateChannelRequest createPrivateChannelRequest);
    Channel find(UUID channelId);
    List<Channel> findAllByUserId(UUID userId);
    Channel update(UUID channelId, UpdateChannelRequest updateChannelRequest);
    void delete(UUID channelId);
    void addParticipant(UUID channelId, UUID userId);
    void deleteParticipant(UUID channelId, UUID userId);
}
