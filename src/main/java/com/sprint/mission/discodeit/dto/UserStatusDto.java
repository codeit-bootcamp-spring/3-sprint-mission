package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UserStatusDto(
        UUID id,
        UUID userId,
        Instant lastActiveAt

) {
}
