package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
    @NotBlank
    @Size(min = 1 , max =50)
    String username,

    @NotNull
    @Email
    @Size(max = 100)
    String email,

    @NotBlank
    @Size(min = 4, max = 50)
    String password
) {

}
