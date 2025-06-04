package com.sprint.mission.discodeit.dto.data;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.UUID;

public record UserStatusDto(
        UUID id,
        UUID userId,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant lastActiveAt
) {
}
