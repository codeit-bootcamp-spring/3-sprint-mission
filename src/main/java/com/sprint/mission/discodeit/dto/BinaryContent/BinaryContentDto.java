package com.sprint.mission.discodeit.dto.BinaryContent;

import java.util.UUID;

public record BinaryContentDto(
        UUID id,
        String fileName,
        Long size,
        String contentType
) {

}
