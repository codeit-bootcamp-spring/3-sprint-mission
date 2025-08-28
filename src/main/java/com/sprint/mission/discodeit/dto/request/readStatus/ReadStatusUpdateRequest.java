package com.sprint.mission.discodeit.dto.request.readStatus;

import java.time.Instant;

public record ReadStatusUpdateRequest(
    Instant newLastReadAt,
    Boolean newNotificationEnabled
) {
}
