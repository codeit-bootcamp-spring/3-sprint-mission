package com.sprint.mission.discodeit.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Objects;

public record UserStatusUpdateRequest(
    @NotBlank Instant newLastActiveAt
) {
    public UserStatusUpdateRequest {
        Objects.requireNonNull(newLastActiveAt, "newLastActiveAt must not be null");
    }
}
