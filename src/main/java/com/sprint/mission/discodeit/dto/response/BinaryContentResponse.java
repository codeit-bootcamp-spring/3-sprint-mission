package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.time.Instant;
import java.util.UUID;

public record BinaryContentResponse(
    UUID id,
    Instant createdAt,
    String fileName,
    byte[] bytes,
    String contentType
) {

    public static BinaryContentResponse fromEntity(BinaryContent file) {
        return new BinaryContentResponse(
            file.getId(),
            file.getCreatedAt(),
            file.getFileName(),
            file.getBytes(),
            file.getContentType()
        );
    }
}
