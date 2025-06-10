package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;
import lombok.Builder;

@Builder
public record BinaryContentResponse(
    UUID id,
    String fileName,
    Long size,
    String contentType
) {

}
