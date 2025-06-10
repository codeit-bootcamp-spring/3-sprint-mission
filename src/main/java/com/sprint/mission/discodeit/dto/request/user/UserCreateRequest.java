package com.sprint.mission.discodeit.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public record UserCreateRequest(
    @NotBlank String username,
    @NotBlank @Email String email,
    @NotBlank String password
) {

    public UserCreateRequest {
        Objects.requireNonNull(username, "username must not be null");
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(password, "password must not be null");
    }
}
