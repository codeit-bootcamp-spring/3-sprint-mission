package com.sprint.mission.discodeit.dto.User;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UserDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String email,
        UUID profileId,
        Boolean online
) {
}