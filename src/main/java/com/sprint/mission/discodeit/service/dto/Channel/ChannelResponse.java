package com.sprint.mission.discodeit.service.dto.Channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponse(
        UUID id,
        ChannelType type,
        String name,
        String description,
        Instant lastMessageAt,
        List<UUID> participantIds
) {
}
