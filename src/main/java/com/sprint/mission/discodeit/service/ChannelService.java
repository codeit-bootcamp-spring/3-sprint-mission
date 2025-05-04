package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.dto.ChannelDTO;
import com.sprint.mission.discodeit.entity.dto.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.entity.dto.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.entity.dto.UpdateChannelRequest;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ChannelService {
    Channel create(CreatePrivateChannelRequest request);
    Channel create(CreatePublicChannelRequest request);
    ChannelDTO find(UUID id);
    List<ChannelDTO> findAllByUserId(UUID userId);
    Channel update(UpdateChannelRequest request);
    void delete(UUID channelId);
}
