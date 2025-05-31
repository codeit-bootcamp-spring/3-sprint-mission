package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record ReadStatusCreateRequest(
    @NotBlank UUID userId,
    @NotBlank UUID channelId,
    @NotBlank Instant lastReadAt
) {
    public ReadStatusCreateRequest {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(channelId, "channelId must not be null");
        Objects.requireNonNull(lastReadAt, "lastReadAt must not be null");
    }
}
