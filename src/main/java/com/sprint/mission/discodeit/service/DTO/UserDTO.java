package com.sprint.mission.discodeit.service.DTO;

import java.time.Instant;
import java.util.UUID;

public record UserDTO (
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String userName,
        String email,
        UUID portraitId,
        Boolean online
){}
