package com.sprint.mission.discodeit.service.dto.BinaryContent;

public record BinaryContentCreateRequest(
        String fileName,
        String contentType,
        byte[] bytes
) {
}
