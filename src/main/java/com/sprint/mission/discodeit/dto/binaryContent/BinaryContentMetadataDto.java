package com.sprint.mission.discodeit.dto.binaryContent;

import java.time.LocalDateTime;
import java.util.UUID;

public record BinaryContentMetadataDto(
        UUID id,
        LocalDateTime createdAt,
        String fileName,
        Long size,
        String contentType) {
}