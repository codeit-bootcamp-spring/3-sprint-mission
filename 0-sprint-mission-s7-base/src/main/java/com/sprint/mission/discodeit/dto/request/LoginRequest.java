package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
    @NotBlank
    @NotNull
    String username,

    @NotNull
    @NotBlank
    String password
) {

}
