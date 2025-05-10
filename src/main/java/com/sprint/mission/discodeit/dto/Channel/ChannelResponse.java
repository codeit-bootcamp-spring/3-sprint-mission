package com.sprint.mission.discodeit.dto.Channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ChannelResponse(
        UUID id,
        ChannelType type,
        String name,
        String description,
        Instant lastMessageAt,
        List<UUID> participantIds
) {
}
