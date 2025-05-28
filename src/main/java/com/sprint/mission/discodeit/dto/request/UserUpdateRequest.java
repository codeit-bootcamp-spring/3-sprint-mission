package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequest(
    @NotBlank String newUsername,
    @NotBlank String newEmail,
    @NotBlank String newPassword
) {

}
