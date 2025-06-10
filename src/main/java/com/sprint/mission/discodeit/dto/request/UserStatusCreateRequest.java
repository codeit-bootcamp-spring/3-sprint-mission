package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record UserStatusCreateRequest(
    @NotNull UUID userId,
    @NotNull Instant lastActiveAt
) {

    public UserStatusCreateRequest {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(lastActiveAt, "lastActiveAt must not be null");
    }
}
