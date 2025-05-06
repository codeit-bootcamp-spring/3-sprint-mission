package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public record UserDTO(
        UUID id,
        String username,
        String email,
        String name,
        UUID profileId,
        boolean isLogin,
        Instant createdAt,
        Instant updatedAt,
        Instant loginTime

) {
}
