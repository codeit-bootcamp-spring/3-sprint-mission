package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createPublic(PublicChannelCreateRequest request);
    Channel createPrivate(PrivateChannelCreateRequest request);
    ChannelDto find(UUID channelId);
    List<ChannelDto> findAll();
    Channel update(UUID channelId, PublicChannelUpdateRequest request);
    void delete(UUID channelId);
}
