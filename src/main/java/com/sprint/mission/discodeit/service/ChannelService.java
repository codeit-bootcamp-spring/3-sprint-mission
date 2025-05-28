package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateDto;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.*;

public interface ChannelService {

  Channel createPublicChannel(PublicChannelDto publicChannelDTO);

  Channel createPrivateChannel(PrivateChannelDto privateChannelDTO);

  ChannelResponseDto findById(UUID channelId);

  List<ChannelResponseDto> findAllByUserId(UUID userId);

  ChannelResponseDto update(UUID channelId, PublicChannelUpdateDto publicChannelUpdateDTO);

  void deleteById(UUID channelId);
}
