package com.sprint.mission.discodeit.event;

import java.time.Instant;
import java.util.UUID;

public record UserLogInOutEvent(
        UUID userId,
        boolean loginNow
) {
}
