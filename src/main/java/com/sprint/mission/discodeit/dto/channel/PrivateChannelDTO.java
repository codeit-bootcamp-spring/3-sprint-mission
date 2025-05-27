package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(description = "Private Channel 생성 정보")
public record PrivateChannelDTO(UUID channelMaster, List<UUID> participantIds) {

//  public static Channel toEntity(PrivateChannelDTO privateChannelDTO) {
//    Channel channel = new Channel(privateChannelDTO.channelMaster(),
//        privateChannelDTO.participantIds());
//
//    return channel;
//  }
}
