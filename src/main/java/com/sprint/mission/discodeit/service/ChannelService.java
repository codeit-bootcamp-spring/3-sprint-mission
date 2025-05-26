package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entitiy.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

  Channel create(PublicChannelCreateRequest publicChannelCreateRequest);

  Channel create(PrivateChannelCreateRequest privateChannelCreateRequest);

  List<ChannelDto> findAllByUserId(UUID userId);

  ChannelDto find(UUID id);

  void update(UUID channelId, PublicChannelUpdateRequest request);

  void delete(UUID channelId);
}
