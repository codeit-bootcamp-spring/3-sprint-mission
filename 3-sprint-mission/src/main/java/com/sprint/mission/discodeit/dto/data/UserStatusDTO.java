package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.UUID;

public record UserStatusDTO(

    UUID id,
    UUID userId,
    Instant lastActiveAt
) {

}
