package com.sprint.mission.discodeit.dto.auth;

import com.sprint.mission.discodeit.dto.user.UserDto;

/**
 * PackageName  : com.sprint.mission.discodeit.dto.auth
 * FileName     : JwtDto
 * Author       : dounguk
 * Date         : 2025. 8. 14.
 */
public record JwtDto(
    UserDto userDto,
    String accessToken
) {
}