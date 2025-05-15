package com.sprint.mission.discodeit.dto.BinaryContent;

public record BinaryContentCreateRequest(
        String fileName,
        String contentType,
        byte[] bytes
) {
}
