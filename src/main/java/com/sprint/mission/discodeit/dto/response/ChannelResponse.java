package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.UUID;

public record ChannelResponse(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    ChannelType type,
    String name,
    String description) {
}