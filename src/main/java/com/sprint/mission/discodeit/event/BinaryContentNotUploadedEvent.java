package com.sprint.mission.discodeit.event;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentNotUploadedEvent (
        String requestId,
        UUID binaryContentId,
        String reason,
        Instant occurredAt
){
}
