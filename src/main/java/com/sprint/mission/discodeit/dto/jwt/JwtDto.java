package com.sprint.mission.discodeit.dto.jwt;

import com.sprint.mission.discodeit.dto.user.UserResponseDto;

public record JwtDto(
    UserResponseDto userDto,
    String accessToken
) {

}
