package com.sprint.mission.discodeit.dto.jwt;

import com.sprint.mission.discodeit.dto.data.UserDto;

public record JwtDto(
    UserDto userDto,
    String accessToken
) {

    public static JwtDto of(UserDto userDto, String accessToken) {
        return new JwtDto(userDto, accessToken);
    }
}
