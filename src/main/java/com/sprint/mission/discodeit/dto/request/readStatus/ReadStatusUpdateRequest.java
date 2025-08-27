package com.sprint.mission.discodeit.dto.request.readStatus;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record ReadStatusUpdateRequest(
    Instant newLastReadAt,
    @NotNull boolean newNotificationEnabled
) {
}
