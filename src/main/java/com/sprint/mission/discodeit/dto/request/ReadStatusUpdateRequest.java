package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Objects;

public record ReadStatusUpdateRequest(
    @NotBlank Instant newLastReadAt
) {
    public ReadStatusUpdateRequest {
        Objects.requireNonNull(newLastReadAt, "newLastReadAt must not be null");
    }
}
