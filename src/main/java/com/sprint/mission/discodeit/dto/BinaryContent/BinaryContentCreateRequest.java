package com.sprint.mission.discodeit.dto.BinaryContent;

import lombok.Builder;

@Builder
public record BinaryContentCreateRequest(
        String fileName,
        String contentType,
        byte[] bytes
) {
}
