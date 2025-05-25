package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
    UUID id,
    ChannelType type,
    String name,
    String description,
    List<UUID> participantIds
) {

  public static ChannelDto fromEntity(Channel channel, List<UUID> participantIds) {
    if (channel == null) {
      return null;
    }
    // 공개채널: name/description 있음, 참가자X
    // 비공개채널: name/description=null, 참가자 있음
    return new ChannelDto(
        channel.getId(),
        channel.getType(),
        channel.getType() == ChannelType.PUBLIC ? channel.getName() : null,
        channel.getType() == ChannelType.PUBLIC ? channel.getDescription() : null,
        channel.getType() == ChannelType.PRIVATE ? (participantIds != null ? participantIds
            : Collections.emptyList()) : null
    );
  }
}
