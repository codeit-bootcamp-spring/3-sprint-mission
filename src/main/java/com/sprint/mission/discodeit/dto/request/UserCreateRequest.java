package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
    @NotBlank
    @Size(min = 4, max = 12)
    String username,

    @NotBlank
    @Email
    String email,

    @NotBlank
    String password
) {

}