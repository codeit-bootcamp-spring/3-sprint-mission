package com.sprint.mission.discodeit.dto.data;

import java.util.UUID;

public record BinaryContentDto(
    UUID id,
    String fileName,
    Long size,
    String contentType
) {
    public BinaryContentDto(UUID id, String contentType, String file, int i) {
        this(id, file, (long) i, contentType);
    }
}

