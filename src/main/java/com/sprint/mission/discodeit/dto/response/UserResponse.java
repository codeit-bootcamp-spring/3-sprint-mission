package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(

    UUID id,
    Instant creatAt,
    Instant updatedAt,
    String username,
    String email,
    UUID profileImageId,
    Boolean isOnline
) {

}
