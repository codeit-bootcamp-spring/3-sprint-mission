package com.sprint.mission.discodeit.dto.binarycontent;

import java.util.UUID;

public record BinaryContentResponse(
        UUID id,
        String contentType,
        String originalFilename,
        String url
) {
}
