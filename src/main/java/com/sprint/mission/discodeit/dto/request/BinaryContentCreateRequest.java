package com.sprint.mission.discodeit.dto.request;

public record BinaryContentCreateRequest(
        String fileName,
        String contentType,
        long   size,
        byte[] bytes
) {}
