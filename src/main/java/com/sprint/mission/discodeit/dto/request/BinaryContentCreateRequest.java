package com.sprint.mission.discodeit.dto.request;

public record BinaryContentCreateRequest(
        String fileName,
        byte[] data,
        String contentType
) {}