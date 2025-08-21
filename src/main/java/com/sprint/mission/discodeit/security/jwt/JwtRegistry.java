package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.data.JwtInformation;

public interface JwtRegistry {

  void registerJwtInformation(JwtInformation jwtInformation);

  boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

  void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation);
}
