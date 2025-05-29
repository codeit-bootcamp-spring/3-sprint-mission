package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.Channel.ChannelFindRequest;
import com.sprint.mission.discodeit.dto.Channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.Channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelDto;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    public ChannelDto create(PublicChannelCreateRequest request);

    public ChannelDto create(PrivateChannelCreateRequest request);

    public ChannelDto find(ChannelFindRequest request);

    public List<ChannelDto> findAllByUserId(UUID userId);

    public ChannelDto update(UUID id, ChannelUpdateRequest request);

    public void delete(UUID channelId);
}
