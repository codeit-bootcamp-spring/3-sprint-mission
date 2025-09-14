package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record SseMessage(
    UUID id,
    long seq,
    String eventName,
    Object data,
    Set<UUID> receivers,
    Instant createdAt
) {}