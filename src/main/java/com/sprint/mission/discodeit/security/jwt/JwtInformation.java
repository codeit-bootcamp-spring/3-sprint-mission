package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.UserDto;

public record JwtInformation(
        UserDto userDto,
        String accessToken,
        String refreshToken
) {
    
}