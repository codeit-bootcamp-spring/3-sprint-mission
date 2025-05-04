package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
        UUID id,
        String name,
        String description,
        String type,
        Instant latestMessageTimestamp,
        List<UUID> memberIds
) {}