package com.sprint.mission.discodeit.security.jwt;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.scheduling.annotation.Scheduled;


public class InMemoryJwtRegistry implements JwtRegistry {

  private final Map<UUID, JwtInformation> origin = new ConcurrentHashMap<>();
  private final Set<String> accessTokenIndexes = ConcurrentHashMap.newKeySet();
  private final Set<String> refreshTokenIndexes = ConcurrentHashMap.newKeySet();

  private final JwtTokenProvider tokenProvider;

  public InMemoryJwtRegistry(JwtTokenProvider tokenProvider) {
    this.tokenProvider = tokenProvider;
  }

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    JwtInformation old = origin.put(jwtInformation.getUserDto().id(), jwtInformation);
    if (old != null) {
      removeTokenIndex(old.getAccessToken(), old.getRefreshToken());
    }
    addTokenIndex(jwtInformation.getAccessToken(), jwtInformation.getRefreshToken());
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    JwtInformation removed = origin.remove(userId);
    if (removed != null) {
      removeTokenIndex(removed.getAccessToken(), removed.getRefreshToken());
    }
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    return origin.containsKey(userId);
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    return accessTokenIndexes.contains(accessToken);
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    return refreshTokenIndexes.contains(refreshToken);
  }

  @Override
  public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
    JwtInformation jwtInformation = origin.get(newJwtInformation.getUserDto().id());
    if (jwtInformation != null && jwtInformation.getRefreshToken().equals(refreshToken)) {
      removeTokenIndex(jwtInformation.getAccessToken(), jwtInformation.getRefreshToken());
      jwtInformation.rotate(newJwtInformation.getAccessToken(),
          newJwtInformation.getRefreshToken());
      addTokenIndex(newJwtInformation.getAccessToken(), newJwtInformation.getRefreshToken());
    }
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    origin.entrySet().removeIf(entry -> {
      JwtInformation jwtInformation = entry.getValue();
      boolean isExpired = jwtInformation.isAccessTokenExpired(tokenProvider)
          || jwtInformation.isRefreshTokenExpired(tokenProvider);
      if (isExpired) {
        removeTokenIndex(jwtInformation.getAccessToken(), jwtInformation.getRefreshToken());
      }
      return isExpired;
    });
  }

  private void addTokenIndex(String accessToken, String refreshToken) {
    accessTokenIndexes.add(accessToken);
    refreshTokenIndexes.add(refreshToken);
  }

  private void removeTokenIndex(String accessToken, String refreshToken) {
    accessTokenIndexes.remove(accessToken);
    refreshTokenIndexes.remove(refreshToken);
  }
}
