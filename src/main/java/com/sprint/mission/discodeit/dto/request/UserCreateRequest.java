package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest(
    @NotBlank String username,
    @NotBlank String email,
    @NotBlank String password
) {

}