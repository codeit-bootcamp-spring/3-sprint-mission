package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDTO;
import com.sprint.mission.discodeit.dto.entity.Channel;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createPublicChannel(PublicChannelCreateRequest publicChannelCreateRequest);
    Channel createPrivateChannel(PrivateChannelCreateRequest privateChannelCreateRequest);
    ChannelDTO getChannel(UUID id);
    List<ChannelDTO> getAllChannelsByUserId(UUID userId);
    Channel getChannelByName(String name);
    Instant getLastMessageInChannel(UUID channelId);
    void updateChannel(PublicChannelUpdateRequest publicChannelUpdateRequest);
    void joinChannel(UUID userId, UUID channelId);
    void leaveChannel(UUID userId, UUID channelId);
    void deleteChannel(UUID id);
}
