package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Public Channel 생성 및 수정 정보")
public record PublicChannelDTO(String channelName, UUID channelMaster, String description) {

  public static Channel toEntity(PublicChannelDTO publicChannelDTO) {
    Channel channel = new Channel(publicChannelDTO.channelName(),
        publicChannelDTO.channelMaster,
        publicChannelDTO.description());

    return channel;
  }
}
