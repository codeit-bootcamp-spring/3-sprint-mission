package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  public Channel create(PublicChannelCreateRequest request);

  public Channel create(PrivateChannelCreateRequest request);

  public ChannelDto find(UUID id);

  public List<ChannelDto> findAllByUserId(UUID id);

  public Channel update(UUID channelId, PublicChannelUpdateRequest request);

  public void delete(UUID channelId);
}