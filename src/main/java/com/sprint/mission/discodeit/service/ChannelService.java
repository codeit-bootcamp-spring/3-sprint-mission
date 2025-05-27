package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(PrivateChannelCreateRequest privateChannelCreateRequest);
    Channel create(PublicChannelCreateRequest publicChannelCreateRequest);
    ChannelDto find(UUID channelId);
    List<ChannelDto> findAllByUserId(UUID userId);
    boolean isExistName(String name);
    Channel update(UUID channelId, PublicChannelUpdateRequest publicChannelUpdateRequest);
    void delete(UUID channelId);
}
