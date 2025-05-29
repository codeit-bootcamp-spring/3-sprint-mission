package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentResponse(
    UUID id,
    Instant createdAt,
    String fileName,
    Long size,
    String contentType,
    byte[] bytes) {
}