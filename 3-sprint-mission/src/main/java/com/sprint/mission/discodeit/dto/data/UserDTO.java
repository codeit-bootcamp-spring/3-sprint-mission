package com.sprint.mission.discodeit.dto.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record UserDTO(

        UUID id,
        String username,
        String email,
        UUID profileId,
        Boolean online,
        Instant createdAt,
        Instant updatedAt

) {
}
