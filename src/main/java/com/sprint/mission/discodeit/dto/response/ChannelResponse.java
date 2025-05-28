package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.UUID;

public record ChannelResponse(
    UUID id,
    ChannelType type,
    String name,
    String description
) {

    public static ChannelResponse fromEntity(Channel channel) {
        return new ChannelResponse(
            channel.getId(),
            channel.getType(),
            channel.getName(),
            channel.getDescription()
        );
    }

}
