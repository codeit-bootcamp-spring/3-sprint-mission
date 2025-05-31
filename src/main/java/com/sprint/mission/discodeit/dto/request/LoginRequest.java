package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public record LoginRequest(
    @NotBlank String username,
    @NotBlank String password
) {
    public LoginRequest {
        Objects.requireNonNull(username, "username must not be null");
        Objects.requireNonNull(password, "password must not be null");
    }
}
