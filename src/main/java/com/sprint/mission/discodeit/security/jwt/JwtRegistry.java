package com.sprint.mission.discodeit.security.jwt;

import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.security.jwt
 * FileName     : JwtRegistry
 * Author       : dounguk
 * Date         : 2025. 8. 17.
 */
public interface JwtRegistry {

    void registerJwtInformation(JwtInformation jwtInformation);

    void invalidateJwtInformationByUserId(UUID userId);

    boolean hasActiveJwtInformationByUserId(UUID userId);

    boolean hasActiveJwtInformationByAccessToken(String accessToken);

    boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

    void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation);

    void clearExpiredJwtInformation();
}
