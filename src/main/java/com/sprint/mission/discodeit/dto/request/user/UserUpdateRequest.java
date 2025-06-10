package com.sprint.mission.discodeit.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public record UserUpdateRequest(
    @NotBlank String newUsername,
    @NotBlank @Email String newEmail,
    @NotBlank String newPassword
) {

    public UserUpdateRequest {
        Objects.requireNonNull(newUsername, "newUsername must not be null");
        Objects.requireNonNull(newEmail, "newEmail must not be null");
        Objects.requireNonNull(newPassword, "newPassword must not be null");
    }

}
