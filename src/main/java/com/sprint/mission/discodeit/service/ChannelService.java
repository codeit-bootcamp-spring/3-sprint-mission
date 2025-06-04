package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.Channel.ChannelDto;
import com.sprint.mission.discodeit.dto.Channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

    ChannelDto create(PublicChannelCreateRequest createRequest);

    ChannelDto create(PrivateChannelCreateRequest createRequest);

    List<ChannelDto> findAllByUserId(UUID userId);

    ChannelDto find(UUID channelId);

    ChannelDto update(UUID channelId, PublicChannelUpdateRequest updateRequest);

    void delete(UUID channelId);
}
