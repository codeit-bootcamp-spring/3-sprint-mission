package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record ChannelDTO(
        UUID id,
        String channelName,
        UUID makerId,
        ChannelType channelType,
        Instant lastMessageAt
) {
}
