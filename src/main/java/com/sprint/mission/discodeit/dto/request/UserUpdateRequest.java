package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRequest(
        @NotNull
        String newUsername,

        @Email
        String newEmail,

        String newPassword
) {
}
