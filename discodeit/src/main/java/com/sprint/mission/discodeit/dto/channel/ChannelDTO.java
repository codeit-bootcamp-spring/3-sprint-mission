package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDTO(UUID id,
                         Instant createdAt,
                         String channelName,
                         ChannelType type,
                         String description,
                         List<UUID> participantIds,
                         Instant lastMessageAt) {
}
