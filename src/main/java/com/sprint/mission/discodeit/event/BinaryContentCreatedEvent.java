package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.time.Instant;

public record BinaryContentCreatedEvent(
        BinaryContent binaryContent,
        boolean isStored,
        Instant occurredAt
) {
    public static BinaryContentCreatedEvent now(BinaryContent binaryContent, boolean isStored) {
        return new BinaryContentCreatedEvent(binaryContent, isStored, Instant.now());
    }
}
