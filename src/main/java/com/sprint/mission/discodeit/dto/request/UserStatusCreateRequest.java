package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequest(
    @NotBlank
    UUID userId,
    @NotBlank
    Instant lastActiveAt
) {

}
