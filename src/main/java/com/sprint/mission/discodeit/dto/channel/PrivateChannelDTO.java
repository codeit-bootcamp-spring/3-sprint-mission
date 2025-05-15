package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public record PrivateChannelDTO(UUID channelMaster, List<UUID> users) {

  public static Channel toEntity(PrivateChannelDTO privateChannelDTO) {
    Channel channel = new Channel(privateChannelDTO.channelMaster(),
        privateChannelDTO.users());

    return channel;
  }
}
