package com.sprint.mission.discodeit.repository.registry;

import com.sprint.mission.discodeit.dto.data.JwtInformation;

import java.util.UUID;

public interface JwtRegistry {
    void register(JwtInformation jwtInformation);
    void inValidJwtInformationByUserId(UUID userId);
    boolean hasActiveJwtInformationByUserId(UUID userId);
    boolean hasActiveJwtInformationByAccessToken(String accessToken);
    boolean hasActiveJwtInformationByRefreshToken(String refreshToken);
    JwtInformation rotateJwtInformation(String refreshToken, JwtInformation newjwtInformation);
}
