package com.sprint.mission.discodeit.dto.binarycontent;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentResponseDTO(UUID id, Instant createdAt, String fileName,
                                       String contentType,
                                       byte[] bytes) {

}
