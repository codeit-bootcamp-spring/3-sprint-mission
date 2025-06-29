package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
    @NotBlank @Size(min = 3, max = 20) String username,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8, max = 30) String password
) {}
