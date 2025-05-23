package com.sprint.mission.discodeit.dto.binaryContent;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentMetadataDto(
    UUID id,
    Instant createdAt,
    String fileName,
    Long size,
    String contentType
) {
} 