package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;

public record ReadStatusUpdateRequest(
    @NotNull Instant newLastReadAt
) {

    public ReadStatusUpdateRequest {
        Objects.requireNonNull(newLastReadAt, "newLastReadAt must not be null");
    }
}
