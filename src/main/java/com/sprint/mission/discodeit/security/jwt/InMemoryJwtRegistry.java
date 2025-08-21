package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.data.JwtInformation;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InMemoryJwtRegistry implements JwtRegistry {

  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final Set<String> accessTokenIndexes = ConcurrentHashMap.newKeySet();
  private final Set<String> refreshTokenIndexes = ConcurrentHashMap.newKeySet();

  private final int maxActiveJwtCount;

  public InMemoryJwtRegistry(int maxActiveJwtCount) {
    this.maxActiveJwtCount = maxActiveJwtCount;
  }

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    origin.compute(jwtInformation.getUserDto().id(), (key, queue) -> {
      if (queue == null) {
        queue = new ConcurrentLinkedQueue<>();
      }
      // 최대 허용 토큰 개수를 초과하면 가장 오래된 토큰을 제거
      if (queue.size() >= maxActiveJwtCount) {
        JwtInformation deprecatedJwtInformation = queue.poll(); // 가장 오래된 토큰 제거
        if (deprecatedJwtInformation != null) {
          removeTokenIndex(
              deprecatedJwtInformation.getAccessToken(),
              deprecatedJwtInformation.getRefreshToken()
          );
        }
      }
      queue.add(jwtInformation); // 새 토큰 등록
      addTokenIndex(
          jwtInformation.getAccessToken(),
          jwtInformation.getRefreshToken()
      );
      return queue;
    });
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

  private void addTokenIndex(String accessToken, String refreshToken) {
    accessTokenIndexes.add(accessToken);
    refreshTokenIndexes.add(refreshToken);
  }

  private void removeTokenIndex(String accessToken, String refreshToken) {
    accessTokenIndexes.remove(accessToken);
    refreshTokenIndexes.remove(refreshToken);
  }
}
