package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.UUID;

public record UserStatusResponse(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    UUID userId,
    Instant lastActiveAt
) {

    public static UserStatusResponse fromEntity(UserStatus us) {
        return new UserStatusResponse(
            us.getId(),
            us.getCreatedAt(),
            us.getUpdatedAt(),
            us.getUserId(),
            us.getLastActiveAt()
        );
    }
}
