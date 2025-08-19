package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String username,
    String email,
    BinaryContentResponse profile,
    Boolean online,
    Role role
) {

}