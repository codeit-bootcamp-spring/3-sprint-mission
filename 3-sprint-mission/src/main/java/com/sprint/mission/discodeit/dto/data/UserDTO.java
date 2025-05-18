package com.sprint.mission.discodeit.dto.data;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record UserDTO(
        UUID id,
        String username,
        String email,
        String name,
        UUID profileId,
        Boolean online,
        Instant createdAt,
        Instant updatedAt

) {
}
