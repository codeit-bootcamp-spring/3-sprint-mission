package com.sprint.mission.discodeit.service.dto.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequest(

        UUID userId,
        Instant recentStatusAt
) {

}
