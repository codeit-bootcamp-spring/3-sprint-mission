package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  ChannelResponse createPublicChannel(PublicChannelCreateRequest request);

  ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request);

  ChannelDto find(UUID channelId);

  List<ChannelDto> findAllByUserId(UUID userId);

  ChannelResponse update(UUID channelId, PublicChannelUpdateRequest request);

  ChannelResponse delete(UUID channelId);

}
