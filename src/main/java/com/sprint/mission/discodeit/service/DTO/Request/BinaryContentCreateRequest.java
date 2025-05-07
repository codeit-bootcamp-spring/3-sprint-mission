package com.sprint.mission.discodeit.service.DTO.Request;

public record BinaryContentCreateRequest(
    String fileName,
    String contentType,
    byte[] content
) {}
