package com.sprint.mission.discodeit.dto.data;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
                UUID id,
                LocalDateTime createdAt,
                LocalDateTime updatedAt,
                String username,
                String email,
                UUID profileId,
                Boolean online) {
}
