package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @Size(min = 3, max = 20)
    String newUsername,
    @Email
    String newEmail,
    @Size(min = 6, max = 30)
    String newPassword
) {

}