package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public record BinaryContentCreateRequest(
    @NotBlank String fileName,
    @NotBlank String contentType,
    @NotBlank byte[] bytes
) {

    public BinaryContentCreateRequest {
        Objects.requireNonNull(fileName, "fileName must not be null");
        Objects.requireNonNull(contentType, "contentType must not be null");
        Objects.requireNonNull(bytes, "bytes must not be null");
    }
}
