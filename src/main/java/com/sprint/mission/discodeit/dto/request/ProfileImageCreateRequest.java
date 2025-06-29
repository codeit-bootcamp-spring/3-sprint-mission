package com.sprint.mission.discodeit.dto.request;

public record ProfileImageCreateRequest(
        String fileName,
        String contentType,
        byte[] bytes
) { }
