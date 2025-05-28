package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDTO;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelDTO;
import com.sprint.mission.discodeit.dto.channel.PublicChannelDTO;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateDTO;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.*;

public interface ChannelService {

  Channel createPublicChannel(PublicChannelDTO publicChannelDTO);

  Channel createPrivateChannel(PrivateChannelDTO privateChannelDTO);

  ChannelResponseDTO findById(UUID channelId);

  List<ChannelResponseDTO> findAllByUserId(UUID userId);

  ChannelResponseDTO update(UUID channelId, PublicChannelUpdateDTO publicChannelUpdateDTO);

  void deleteById(UUID channelId);
}
