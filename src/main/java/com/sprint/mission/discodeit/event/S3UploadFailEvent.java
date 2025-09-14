package com.sprint.mission.discodeit.event;

import java.time.Instant;
import java.util.UUID;

public record S3UploadFailEvent (
    UUID binaryContentId,
    String requestId,
    String errorMsg,
    Instant occurredAt
) {
    public static S3UploadFailEvent now(UUID binaryContentId, String requestId, String errorMsg) {
        return new S3UploadFailEvent(binaryContentId, requestId, errorMsg, Instant.now());
    }
}
