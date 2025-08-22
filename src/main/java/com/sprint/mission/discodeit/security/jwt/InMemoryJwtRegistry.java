package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.data.JwtInformation;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.scheduling.annotation.Scheduled;

public class InMemoryJwtRegistry implements JwtRegistry {

  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final Set<String> accessTokenIndexes = ConcurrentHashMap.newKeySet();
  private final Set<String> refreshTokenIndexes = ConcurrentHashMap.newKeySet();

  private final int maxActiveJwtCount;
  private final JwtTokenProvider tokenProvider;

  public InMemoryJwtRegistry(int maxActiveJwtCount, JwtTokenProvider tokenProvider) {
    this.maxActiveJwtCount = maxActiveJwtCount;
    this.tokenProvider = tokenProvider;
  }

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    origin.compute(jwtInformation.getUserDto().id(), (key, queue) -> {
      if (queue == null) {
        queue = new ConcurrentLinkedQueue<>();
      }
      // 최대 허용 토큰 개수를 초과하면 가장 오래된 토큰을 제거
      if (queue.size() >= maxActiveJwtCount) {
        JwtInformation deprecatedJwtInformation = queue.poll();
        if (deprecatedJwtInformation != null) {
          removeTokenIndex(
              deprecatedJwtInformation.getAccessToken(),
              deprecatedJwtInformation.getRefreshToken()
          );
        }
      }
      queue.add(jwtInformation);
      addTokenIndex(
          jwtInformation.getAccessToken(),
          jwtInformation.getRefreshToken()
      );
      return queue;
    });
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    Queue<JwtInformation> removed = origin.remove(userId);
    if (removed != null) {
      removed.forEach(info -> removeTokenIndex(info.getAccessToken(), info.getRefreshToken()));
    }
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    Queue<JwtInformation> queue = origin.get(userId);
    return queue != null && !queue.isEmpty();
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
    origin.computeIfPresent(newJwtInformation.getUserDto().id(), (key, queue) -> {
      queue.stream().filter(jwtInformation -> jwtInformation.getRefreshToken().equals(refreshToken))
          .findFirst()
          .ifPresent(jwtInformation -> {
            removeTokenIndex(jwtInformation.getAccessToken(), jwtInformation.getRefreshToken());
            jwtInformation.rotate(
                newJwtInformation.getAccessToken(),
                newJwtInformation.getRefreshToken()
            );
            addTokenIndex(
                newJwtInformation.getAccessToken(),
                newJwtInformation.getRefreshToken()
            );
          });
      return queue;
    });
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    origin.entrySet().removeIf(entry -> {
      Queue<JwtInformation> queue = entry.getValue();
      queue.removeIf(jwtInformation -> {
        boolean isExpired =
            !tokenProvider.validateAccessToken(jwtInformation.getAccessToken()) ||
                !tokenProvider.validateRefreshToken(jwtInformation.getRefreshToken());
        if (isExpired) {
          removeTokenIndex(
              jwtInformation.getAccessToken(),
              jwtInformation.getRefreshToken()
          );
        }
        return isExpired;
      });
      return queue.isEmpty();
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
