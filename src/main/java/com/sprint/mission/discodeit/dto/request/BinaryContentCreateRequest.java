package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BinaryContentCreateRequest(

    @NotBlank
    @Size(min = 1, max = 255)
    String fileName,

    @NotBlank
    @Size(min = 1, max = 100)
    String contentType,

    @NotNull
    byte[] bytes

) { }
