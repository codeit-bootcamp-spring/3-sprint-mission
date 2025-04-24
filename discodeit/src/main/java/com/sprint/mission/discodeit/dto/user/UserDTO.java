package com.sprint.mission.discodeit.dto.user;

import java.time.Instant;
import java.util.UUID;

public record UserDTO(UUID id,
                      Instant cratedAt,
                      Instant updatedAt,
                      String username,
                      String email,
                      boolean online) {
}
