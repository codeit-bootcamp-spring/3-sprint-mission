package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record ReadStatusCreateRequest(
    @NotBlank UUID userId,
    @NotBlank UUID channelId,
    @NotNull Instant lastReadAt
) {

    public ReadStatusCreateRequest {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(channelId, "channelId must not be null");
        Objects.requireNonNull(lastReadAt, "lastReadAt must not be null");
    }
}
