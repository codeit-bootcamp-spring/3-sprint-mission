package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  ChannelResponse createPublicChannel(PublicChannelCreateRequest request);

  ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request);

  ChannelResponse find(UUID channelId);

  List<ChannelResponse> findAllByUserId(UUID userId);

  ChannelResponse update(UUID channelId, PublicChannelUpdateRequest request);

  ChannelResponse delete(UUID channelId);

}
