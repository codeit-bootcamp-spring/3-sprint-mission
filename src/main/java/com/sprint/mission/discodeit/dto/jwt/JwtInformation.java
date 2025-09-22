package com.sprint.mission.discodeit.dto.jwt;

import com.sprint.mission.discodeit.dto.data.UserDto;

public record JwtInformation(
    UserDto userDto,
    String accessToken,
    String refreshToken
) {

    public JwtInformation rotate(String accessToken, String refreshToken) {
        return new JwtInformation(this.userDto, accessToken, refreshToken);
    }

}
