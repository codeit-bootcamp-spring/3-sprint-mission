package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelMemberRequestDTO;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDTO;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelDTO;
import com.sprint.mission.discodeit.dto.channel.PublicChannelDTO;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.*;

public interface ChannelService {
    Channel createPublicChannel(PublicChannelDTO publicChannelDTO);
    Channel createPrivateChannel(PrivateChannelDTO privateChannelDTO);
    ChannelResponseDTO findById(UUID channelId);
    List<ChannelResponseDTO> findByNameContaining(String name);
    List<ChannelResponseDTO> findAllByUserId(UUID userId);
    List<ChannelResponseDTO> findAll();
    ChannelResponseDTO update(UUID channelId, PublicChannelDTO publicChannelDTO);
    void deleteById(UUID channelId);
    void inviteUser(ChannelMemberRequestDTO channelMemberRequestDTO);
    void kickUser(ChannelMemberRequestDTO channelMemberRequestDTO);
}
