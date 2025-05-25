package com.sprint.mission.discodeit.dto.userStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserStatusCreateRequest(
    UUID userId,
    LocalDateTime lastActiveAt) {
}
