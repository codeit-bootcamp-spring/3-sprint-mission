package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponse;
import lombok.Builder;

import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.Dto.user
 * FileName     : JpaUserDto
 * Author       : dounguk
 * Date         : 2025. 5. 29.
 */
@Builder
public record UserResponse(
        UUID id,
        String username,
        String email,
        BinaryContentResponse profile,
        boolean online
) {
}
