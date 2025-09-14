package com.sprint.mission.discodeit.event;

import java.time.Instant;
import java.util.UUID;

public record MessageCreatedEvent(
    UUID messageId,
    UUID channelId,
    UUID authorId,
    Instant occurredAt
) {
    public static MessageCreatedEvent now(UUID messageId, UUID channelId, UUID authorId) {
        return new MessageCreatedEvent(messageId, channelId, authorId, Instant.now());
    }
}
