package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.time.Instant;
import java.util.UUID;

public record UserDto(

    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String username,
    String email,
    BinaryContent profileId,
    Boolean online
) {

}
