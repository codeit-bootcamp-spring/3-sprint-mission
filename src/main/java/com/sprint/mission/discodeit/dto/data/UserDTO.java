package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.UUID;

public record UserDTO(
    UUID id,
    Instant creatAt,
    Instant updatedAt,
    String username,
    String email,
    UUID profileImageId,
    Boolean isOnline

) {


}