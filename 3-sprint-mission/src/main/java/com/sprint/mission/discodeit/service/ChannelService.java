package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDTO;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  // id = channel ID
  ChannelDTO create(PrivateChannelCreateRequest privateChannelCreateDTO);

  ChannelDTO create(PublicChannelCreateRequest publicChannelCreateDTO);

  ChannelDTO find(UUID id);

  List<ChannelDTO> findByName(String name);

  List<ChannelDTO> findAll();

  List<ChannelDTO> findAllByUserId(UUID userId);

  ChannelDTO update(UUID id, PublicChannelUpdateRequest publicChannelUpdateDTO);

  void delete(UUID id);

}
