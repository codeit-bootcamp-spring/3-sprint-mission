package com.sprint.mission.discodeit.security.jwt;

public interface JwtRegistry{
    void registerJwtInformation(JwtInformation jwtInformation);
    void invalidateJwtInformationByUserId(String userId);
    boolean hasActiveJwtInformationByUserId(String userId);
    boolean hasActiveJwtInformationByAccessToken(String accessToken);
    boolean hasActiveJwtInformationByRefreshToken(String refreshToken);
    void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation);
}
