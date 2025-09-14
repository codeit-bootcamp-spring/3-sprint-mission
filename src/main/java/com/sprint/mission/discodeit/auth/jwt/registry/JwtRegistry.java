package com.sprint.mission.discodeit.auth.jwt.registry;

import java.util.UUID;

public interface JwtRegistry {
    void registerJwt(JwtInformation jwtInformation);

    void invalidateJwtInformationByUserId(UUID userId);

    boolean hasActiveJwtInformationByUserId(UUID userId);

    boolean hasActiveJwtInformationByAccessToken(String accessToken);

    boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

    void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation);

    void invalidateByRefreshToken(String refreshToken);
}
