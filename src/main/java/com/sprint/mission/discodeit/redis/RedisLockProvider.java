package com.sprint.mission.discodeit.redis;

import java.time.Duration;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisLockProvider {

    private static final Duration LOCK_TIMEOUT = Duration.ofSeconds(10);
    private static final String LOCK_KEY_PREFIX = "lock:";

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 분산 락 획득
     * @return lockValue (해제 시 필요)
     */
    public String acquireLock(String key) {
        String lockKey = LOCK_KEY_PREFIX + key;
        String lockValue = Thread.currentThread().getName() + "-" + System.currentTimeMillis();

        ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
        Boolean acquired = valueOps.setIfAbsent(lockKey, lockValue, LOCK_TIMEOUT);

        if (Boolean.TRUE.equals(acquired)) {
            log.debug("분산 락 획득 성공: {} (값: {})", lockKey, lockValue);
            return lockValue;
        } else {
            log.debug("분산 락 획득 실패: {}", lockKey);
            throw new RedisLockAcquisitionException("분산 락 획득 실패: " + lockKey);
        }
    }

    /**
     * 분산 락 해제 (소유권 확인 포함)
     */
    public void releaseLock(String key, String lockValue) {
        String lockKey = LOCK_KEY_PREFIX + key;
        try {
            redisTemplate.execute(UNLOCK_SCRIPT, Collections.singletonList(lockKey), lockValue);
            log.debug("분산 락 해제 시도: {}", lockKey);
        } catch (Exception e) {
            log.warn("분산 락 해제 실패: {}", lockKey, e);
        }
    }

    public static class RedisLockAcquisitionException extends RuntimeException {
        public RedisLockAcquisitionException(String message) {
            super(message);
        }
    }

    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT = new DefaultRedisScript<>(
        "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "  return redis.call('del', KEYS[1]) " +
            "else return 0 end", Long.class);
}
