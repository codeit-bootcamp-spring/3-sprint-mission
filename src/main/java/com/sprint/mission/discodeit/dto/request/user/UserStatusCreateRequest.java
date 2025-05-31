package com.sprint.mission.discodeit.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record UserStatusCreateRequest(
    @NotBlank UUID userId,
    @NotBlank Instant lastActiveAt
) {
    public UserStatusCreateRequest {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(lastActiveAt, "lastActiveAt must not be null");
    }
}
