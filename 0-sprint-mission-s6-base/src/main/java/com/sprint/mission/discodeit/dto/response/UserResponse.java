package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;
import lombok.Builder;

@Builder
public record UserResponse(
    UUID id,
    String username,
    String email,
    BinaryContentResponse profile,
    boolean online
) {

}
