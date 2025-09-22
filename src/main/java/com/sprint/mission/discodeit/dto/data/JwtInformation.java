package com.sprint.mission.discodeit.dto.data;

public record JwtInformation(
        UserDto userDto,
        String accessToken,
        String refreshToken
) {
    public JwtInformation rotate(String newAccessToken, String newRefreshToken) {
        return new JwtInformation(this.userDto, newAccessToken, newRefreshToken);
    }
}
