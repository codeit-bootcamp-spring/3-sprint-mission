package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  Channel create(String name);

  ChannelResponse findById(UUID id);

  List<ChannelResponse> findAll();

  List<ChannelResponse> findAllByUserId(UUID userId);

  ChannelResponse update(PublicChannelUpdateRequest request);

  Channel delete(UUID id);

  ChannelResponse createPublicChannel(PublicChannelCreateRequest request);

  ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request);


}
