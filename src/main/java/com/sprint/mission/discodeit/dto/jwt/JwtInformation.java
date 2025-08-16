package com.sprint.mission.discodeit.dto.jwt;

import com.sprint.mission.discodeit.dto.user.UserResponseDto;

public record JwtInformation(
    UserResponseDto userResponseDto,
    String accessToken,
    String refreshToken
) {

    public JwtInformation rotate(String newAccessToken, String newRefreshToken) {
        return new JwtInformation(userResponseDto, newAccessToken, newRefreshToken);
    }
}
