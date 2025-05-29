package com.sprint.mission.discodeit.Dto.user;

import com.sprint.mission.discodeit.Dto.binaryContent.JpaBinaryContentResponse;

import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.Dto.user
 * FileName     : JpaUserDto
 * Author       : dounguk
 * Date         : 2025. 5. 29.
 */
public record JpaUserResponse(
        UUID id,
        String username,
        String email,
        JpaBinaryContentResponse profileDto,
        boolean online
) {
}
