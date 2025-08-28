package com.sprint.mission.discodeit.dto.jwt;

import java.util.UUID;

public record JwtInformation(
    UUID userId,
    String username,
    String accessToken,
    String refreshToken
) {

    public JwtInformation rotate(String newAccessToken, String newRefreshToken) {
        return new JwtInformation(userId, username, newAccessToken, newRefreshToken);
    }
}
