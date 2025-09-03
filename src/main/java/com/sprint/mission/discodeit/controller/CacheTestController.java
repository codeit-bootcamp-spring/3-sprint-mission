package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/test/cache")
@Slf4j
public class CacheTestController {

    private final NotificationService notificationService;
    private final CacheManager cacheManager;

    @PostMapping("/notifications")
    public ResponseEntity<NotificationDto> createTestNotification(
        @RequestParam UUID receiverId,
        @RequestParam String title,
        @RequestParam String content) {

        log.info("[CACHE-TEST] 테스트 알림 생성 - 수신자 : {} ", receiverId);
        NotificationDto notification = notificationService.create(receiverId, title, content);
        return ResponseEntity.ok().body(notification);
    }

    @GetMapping("/notifications/{receiverId}")
    public ResponseEntity<List<NotificationDto>> getNotificationsMultipleTimes(
        @PathVariable UUID receiverId,
        @RequestParam(defaultValue = "3") int times) {

        log.info("[CACHE-TEST] 캐시 테스트 시작 - {}번 연속 호출", times);

        List<NotificationDto> result = null;
        for (int i = 1; i <= times; i++) {
            log.info("[CACHE-TEST] {}번째 호출 시작", i);
            long startTime = System.currentTimeMillis();

            result = notificationService.findAllByReceiverId(receiverId);

            long endTime = System.currentTimeMillis();
            log.info("[CACHE-TEST] {}번째 호출 완료 - 응답시간: {}ms", i, (endTime - startTime));
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/stats")
    public ResponseEntity<String> getCacheStats() {
        var cache = cacheManager.getCache("userNotifications");
        if (cache != null) {
            log.info("[CACHE-STATS] 캐시 상태 조회");
            String cacheType = cache instanceof RedisCache ? "Redis" : cache.getClass().getSimpleName();
            return ResponseEntity.ok("Cache found: userNotifications (Type: " + cacheType + ")");
        }
        return ResponseEntity.ok("No cache found");
    }

    @DeleteMapping("/clear/{receiverId}")
    public ResponseEntity<String> clearCache(@PathVariable UUID receiverId) {
        var cache = cacheManager.getCache("userNotifications");
        if (cache != null) {
            boolean hadValue = cache.get(receiverId) != null;
            cache.evict(receiverId);
            log.info("[CACHE-CLEAR] 캐시 수동 삭제 - 사용자: {}, 기존 값 존재: {}", receiverId, hadValue);
            return ResponseEntity.ok("Cache cleared for user: " + receiverId + ", had value: " + hadValue);
        }
        return ResponseEntity.ok("No cache found");
    }

    @GetMapping("/debug")
    public ResponseEntity<Map<String, Object>> debugCache() {
        Map<String, Object> info = new HashMap<>();

        info.put("cacheManagerType", cacheManager.getClass().getSimpleName());
        Collection<String> cacheNames = cacheManager.getCacheNames();
        info.put("cacheNames", cacheNames);

        // Redis/Caffeine 모두 지원하는 캐시 상태 확인
        Map<String, Object> cacheStatus = new HashMap<>();
        for (String cacheName : cacheNames) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                Map<String, Object> cacheInfo = new HashMap<>();
                cacheInfo.put("exists", true);
                cacheInfo.put("nativeCache", cache.getNativeCache().getClass().getSimpleName());

                // Redis 캐시인지 확인
                if (cache instanceof RedisCache) {
                    RedisCache redisCache = (RedisCache) cache;
                    cacheInfo.put("cacheType", "Redis");
                    cacheInfo.put("name", redisCache.getName());
                }
                // Caffeine 캐시인지 확인 (기존 로직 유지)
                else {
                    Object nativeCache = cache.getNativeCache();
                    if (nativeCache instanceof com.github.benmanes.caffeine.cache.Cache) {
                        var caffeineCache = (com.github.benmanes.caffeine.cache.Cache<?, ?>) nativeCache;
                        try {
                            var stats = caffeineCache.stats();
                            cacheInfo.put("cacheType", "Caffeine");
                            cacheInfo.put("statsEnabled", true);
                            cacheInfo.put("hitCount", stats.hitCount());
                            cacheInfo.put("missCount", stats.missCount());
                            cacheInfo.put("requestCount", stats.requestCount());
                            cacheInfo.put("hitRate", stats.hitRate());
                        } catch (Exception e) {
                            cacheInfo.put("cacheType", "Caffeine");
                            cacheInfo.put("statsEnabled", false);
                            cacheInfo.put("statsError", e.getMessage());
                        }
                    } else {
                        cacheInfo.put("cacheType", "Unknown");
                    }
                }

                cacheStatus.put(cacheName, cacheInfo);
            } else {
                cacheStatus.put(cacheName, Map.of("exists", false));
            }
        }
        info.put("cacheStatus", cacheStatus);

        return ResponseEntity.ok(info);
    }

    // Redis 연결 상태 확인 엔드포인트 추가
    @GetMapping("/redis-status")
    public ResponseEntity<Map<String, Object>> getRedisStatus() {
        Map<String, Object> status = new HashMap<>();

        try {
            Cache testCache = cacheManager.getCache("userNotifications");
            if (testCache != null) {
                // 테스트 데이터 저장/조회
                String testKey = "connection-test";
                String testValue = "Redis connection OK at " + System.currentTimeMillis();
                testCache.put(testKey, testValue);

                Cache.ValueWrapper retrieved = testCache.get(testKey);
                boolean connected = retrieved != null;
                status.put("connected", connected);
                status.put("testResult", connected ? retrieved.get() : "null");
                status.put("cacheType", testCache.getClass().getSimpleName());

                // 테스트 데이터 삭제
                testCache.evict(testKey);
            } else {
                status.put("connected", false);
                status.put("error", "Cache not found");
            }
        } catch (Exception e) {
            status.put("connected", false);
            status.put("error", e.getMessage());
            log.error("[REDIS-STATUS] Redis 연결 테스트 실패", e);
        }

        return ResponseEntity.ok(status);
    }
}