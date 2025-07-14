package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  // id = channel ID
  ChannelDto create(PrivateChannelCreateRequest privateChannelCreateDto);

  ChannelDto create(PublicChannelCreateRequest publicChannelCreateDto);

  ChannelDto find(UUID id);

  List<ChannelDto> findByName(String name);

  List<ChannelDto> findAll();

  List<ChannelDto> findAllByUserId(UUID userId);

  ChannelDto update(UUID id, PublicChannelUpdateRequest publicChannelUpdateDto);

  void delete(UUID id);

}
