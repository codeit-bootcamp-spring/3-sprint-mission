package com.sprint.mission.discodeit.service.dto.ReadStatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequest(
        UUID userId,
        UUID channelId,
        Instant recentReadAt
) {
}
