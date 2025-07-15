package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String username,
    String email,
    BinaryContentDto profile,
    Boolean online
) {

}
