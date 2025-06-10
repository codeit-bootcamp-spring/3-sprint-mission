package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ChannelRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

  ChannelResponse createPublicChannel(ChannelRequest.CreatePublic request);

  ChannelResponse createPrivateChannel(ChannelRequest.CreatePrivate request);

  ChannelResponse find(UUID id);

  List<ChannelResponse> findAllByUserId(UUID userId);

  ChannelResponse update(UUID id, ChannelRequest.Update request);

  void delete(UUID id);
}