package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;

public record UserStatusUpdateRequest(
    @NotNull Instant newLastActiveAt
) {

    public UserStatusUpdateRequest {
        Objects.requireNonNull(newLastActiveAt, "newLastActiveAt must not be null");
    }
}
