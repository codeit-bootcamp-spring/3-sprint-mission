package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.Channel.ChannelFindRequest;
import com.sprint.mission.discodeit.dto.Channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.Channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.Channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    public Channel create(PublicChannelCreateRequest request);

    public Channel create(PrivateChannelCreateRequest request);

    public ChannelResponse find(ChannelFindRequest request);

    public List<ChannelResponse> findAllByUserId(UUID userId);

    public Channel update(ChannelUpdateRequest request);

    public void delete(UUID channelId);
}
