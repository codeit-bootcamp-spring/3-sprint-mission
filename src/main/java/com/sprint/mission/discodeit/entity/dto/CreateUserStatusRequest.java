package com.sprint.mission.discodeit.entity.dto;

import com.sprint.mission.discodeit.entity.User;

import java.time.Instant;
import java.util.UUID;

public record CreateUserStatusRequest(
        UUID id,
        User user,
        Instant lastLoginAt
) {
}
