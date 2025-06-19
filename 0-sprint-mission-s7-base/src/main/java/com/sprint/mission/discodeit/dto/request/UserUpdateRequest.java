package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @NotEmpty
    @Size(min = 1 , max =50)
    String newUsername,

    @Email
    @Size(max = 100)
    String newEmail,

    @Size(min = 4, max = 50)
    String newPassword
) {

}
