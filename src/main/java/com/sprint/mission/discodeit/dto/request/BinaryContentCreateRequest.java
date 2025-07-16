package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BinaryContentCreateRequest(
    @NotBlank @Size(max = 255)
    String fileName,

    @NotBlank
    String contentType,

    @NotBlank
    byte[] bytes
) {

}
