package com.sprint.mission.discodeit.redis;

import com.sprint.mission.discodeit.event.UserLogInOutEvent;
import com.sprint.mission.discodeit.redis.RedisLockProvider.RedisLockAcquisitionException;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.store.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.store.JwtRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@RequiredArgsConstructor
public class RedisJwtRegistry implements JwtRegistry {

  private static final String USER_JWT_KEY_PREFIX = "jwt:user:";
  private static final String ACCESS_TOKEN_INDEX_KEY = "jwt:access_tokens";
  private static final String ACCESS_TOKEN_GRACE_INDEX_KEY = "jwt:access_tokens_grace";
  private static final String REFRESH_TOKEN_INDEX_KEY = "jwt:refresh_tokens";
  private static final Duration DEFAULT_TTL = Duration.ofMinutes(30);
  private static final Duration ROTATION_GRACE_TTL = Duration.ofSeconds(5);

  private final int maxActiveJwtCount;
  private final JwtTokenProvider jwtTokenProvider;
  private final ApplicationEventPublisher eventPublisher;
  private final RedisTemplate<String, Object> redisTemplate;
  private final RedisLockProvider redisLockProvider;

  @CacheEvict(value = "users", key = "'all'")
  @Retryable(retryFor = RedisLockAcquisitionException.class, maxAttempts = 10,
      backoff = @Backoff(delay = 100, multiplier = 2))
  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    String userKey = getUserKey(jwtInformation.userDto().id());
    String lockKey = jwtInformation.userDto().id().toString();

    redisLockProvider.acquireLock(lockKey);
    try {
      Long currentSize = redisTemplate.opsForList().size(userKey);

      while (currentSize != null && currentSize >= maxActiveJwtCount) {
        Object oldestTokenObj = redisTemplate.opsForList().leftPop(userKey);
        if (oldestTokenObj instanceof JwtInformation oldestToken) {
          removeTokenIndex(oldestToken.accessToken(), oldestToken.refreshToken());
        }
        currentSize = redisTemplate.opsForList().size(userKey);
      }

      redisTemplate.opsForList().rightPush(userKey, jwtInformation);
      redisTemplate.expire(userKey, DEFAULT_TTL);
      addTokenIndex(jwtInformation.accessToken(), jwtInformation.refreshToken());

    } finally {
      redisLockProvider.releaseLock(lockKey);
    }

    eventPublisher.publishEvent(
        new UserLogInOutEvent(jwtInformation.userDto().id(), true)
    );
  }

  @CacheEvict(value = "users", key = "'all'")
  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    String userKey = getUserKey(userId);

    List<Object> tokens = redisTemplate.opsForList().range(userKey, 0, -1);
    if (tokens != null) {
      tokens.forEach(tokenObj -> {
        if (tokenObj instanceof JwtInformation jwtInfo) {
          removeTokenIndex(jwtInfo.accessToken(), jwtInfo.refreshToken());
        }
      });
    }

    redisTemplate.delete(userKey);
    eventPublisher.publishEvent(new UserLogInOutEvent(userId, false));
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    String userKey = getUserKey(userId);
    Long size = redisTemplate.opsForList().size(userKey);
    return size != null && size > 0;
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    Boolean inMain = redisTemplate.opsForSet().isMember(ACCESS_TOKEN_INDEX_KEY, accessToken);
    if (Boolean.TRUE.equals(inMain)) return true;

    Boolean inGrace = redisTemplate.opsForSet().isMember(ACCESS_TOKEN_GRACE_INDEX_KEY, accessToken);
    return Boolean.TRUE.equals(inGrace);
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    return Boolean.TRUE.equals(
        redisTemplate.opsForSet().isMember(REFRESH_TOKEN_INDEX_KEY, refreshToken)
    );
  }

  @Retryable(retryFor = RedisLockAcquisitionException.class, maxAttempts = 10,
      backoff = @Backoff(delay = 100, multiplier = 2))
  @Override
  public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
    String userKey = getUserKey(newJwtInformation.userDto().id());
    String lockKey = newJwtInformation.userDto().id().toString();

    redisLockProvider.acquireLock(lockKey);
    try {
      List<Object> tokens = redisTemplate.opsForList().range(userKey, 0, -1);

      if (tokens != null) {
        for (int i = 0; i < tokens.size(); i++) {
          if (tokens.get(i) instanceof JwtInformation jwtInfo &&
              jwtInfo.refreshToken().equals(refreshToken)) {

            // 이전 access 토큰은 잠시 유예 세트에 보관하여 회전 직후 401 방지
            moveAccessTokenToGrace(jwtInfo.accessToken());
            removeTokenIndex(jwtInfo.accessToken(), jwtInfo.refreshToken());
            jwtInfo.rotate(newJwtInformation.accessToken(),
                newJwtInformation.refreshToken());
            redisTemplate.opsForList().set(userKey, i, jwtInfo);
            addTokenIndex(newJwtInformation.accessToken(),
                newJwtInformation.refreshToken());
            redisTemplate.expire(userKey, DEFAULT_TTL);
            break;
          }
        }
      }

    } finally {
      redisLockProvider.releaseLock(lockKey);
    }
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    Set<String> userKeys = redisTemplate.keys(USER_JWT_KEY_PREFIX + "*");

    for (String userKey : userKeys) {
      List<Object> tokens = redisTemplate.opsForList().range(userKey, 0, -1);

      if (tokens != null) {
        boolean hasValidTokens = false;

        for (int i = tokens.size() - 1; i >= 0; i--) {
          if (tokens.get(i) instanceof JwtInformation jwtInfo) {
            boolean isExpired =
                !jwtTokenProvider.validateAccessToken(jwtInfo.accessToken()) ||
                    !jwtTokenProvider.validateRefreshToken(jwtInfo.refreshToken());

            if (isExpired) {
              redisTemplate.opsForList().set(userKey, i, "EXPIRED");
              redisTemplate.opsForList().remove(userKey, 1, "EXPIRED");
              removeTokenIndex(jwtInfo.accessToken(), jwtInfo.refreshToken());
            } else {
              hasValidTokens = true;
            }
          }
        }

        if (!hasValidTokens) {
          redisTemplate.delete(userKey);
        }
      }
    }
  }

  @Override
  public UUID findUserIdByRefreshToken(String refreshToken) {
    // 인덱스에 존재하지 않으면 바로 종료
    Boolean exists = redisTemplate.opsForSet().isMember(REFRESH_TOKEN_INDEX_KEY, refreshToken);
    if (!Boolean.TRUE.equals(exists)) {
      return null;
    }

    // 사용자 키들을 순회하며 매칭되는 refresh 토큰을 찾는다
    Set<String> userKeys = redisTemplate.keys(USER_JWT_KEY_PREFIX + "*");
    if (userKeys == null || userKeys.isEmpty()) {
      return null;
    }

    for (String userKey : userKeys) {
      List<Object> tokens = redisTemplate.opsForList().range(userKey, 0, -1);
      if (tokens == null) continue;
      for (Object obj : tokens) {
        if (obj instanceof JwtInformation info) {
          if (refreshToken.equals(info.refreshToken())) {
            try {
              String raw = userKey.substring(USER_JWT_KEY_PREFIX.length());
              return UUID.fromString(raw);
            } catch (Exception ignore) {
              return null;
            }
          }
        }
      }
    }
    return null;
  }

  private String getUserKey(UUID userId) {
    return USER_JWT_KEY_PREFIX + userId.toString();
  }

  private void addTokenIndex(String accessToken, String refreshToken) {
    // Set에 토큰 추가 (add: 중복되면 무시됨)
    redisTemplate.opsForSet().add(ACCESS_TOKEN_INDEX_KEY, accessToken);
    redisTemplate.opsForSet().add(REFRESH_TOKEN_INDEX_KEY, refreshToken);

    // 인덱스 키에도 만료 시간 설정 (메모리 누수 방지)
    redisTemplate.expire(ACCESS_TOKEN_INDEX_KEY, DEFAULT_TTL);
    redisTemplate.expire(REFRESH_TOKEN_INDEX_KEY, DEFAULT_TTL);
  }

  private void removeTokenIndex(String accessToken, String refreshToken) {
    // Set에서 토큰 제거
    redisTemplate.opsForSet().remove(ACCESS_TOKEN_INDEX_KEY, accessToken);
    redisTemplate.opsForSet().remove(REFRESH_TOKEN_INDEX_KEY, refreshToken);
    redisTemplate.opsForSet().remove(ACCESS_TOKEN_GRACE_INDEX_KEY, accessToken);
  }

  private void moveAccessTokenToGrace(String accessToken) {
    if (accessToken == null) return;
    redisTemplate.opsForSet().add(ACCESS_TOKEN_GRACE_INDEX_KEY, accessToken);
    redisTemplate.expire(ACCESS_TOKEN_GRACE_INDEX_KEY, ROTATION_GRACE_TTL);
  }
}
