package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UserStatusDTO(

    UUID id,
    UUID userId,
    Instant lastActiveAt
) {

}
