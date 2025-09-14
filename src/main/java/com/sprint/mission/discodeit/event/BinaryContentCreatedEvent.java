package com.sprint.mission.discodeit.event;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentCreatedEvent(
    UUID binaryContentId,
    byte[] bytes,
    Instant occurredAt
) {
    /**
     * 현재 시각 기준으로 이벤트를 생성하는 팩토리 메서드.
     */
    public static BinaryContentCreatedEvent now(UUID binaryContentId , byte[] bytes) {
        return new BinaryContentCreatedEvent(binaryContentId, bytes, Instant.now());
    }
}
