package com.sprint.mission.discodeit.dto.BinaryContent;

public record BinaryContentCreateRequestDTO(
        String fileName,
        String contentType,
        byte[] content
) {}