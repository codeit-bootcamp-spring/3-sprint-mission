package com.sprint.mission.discodeit.dto.request.user;

import java.time.Instant;

public record UserStatusUpdateRequest(
        Instant newLastActiveAt
) {
}
