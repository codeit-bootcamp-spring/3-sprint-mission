package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
        UUID id,
        ChannelType type,
        String name,
        String description,
        Instant latestMessageTime,
        List<UUID> participantIds
) {
    public ChannelDto(Channel channel, Instant latestMessageTime, List<UUID> participantIds) {
        this(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                latestMessageTime,
                participantIds
        );
    }
}
