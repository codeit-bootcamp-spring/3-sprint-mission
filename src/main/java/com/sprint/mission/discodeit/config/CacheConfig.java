package com.sprint.mission.discodeit.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import java.time.Duration;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    @Profile("dev")
    public CacheManager devCacheManager() {

        CaffeineCacheManager cacheManager = new CaffeineCacheManager("users", "channelsByUser",
            "notificationsByUser");

        cacheManager.setCaffeine(caffeineConfig());

        return cacheManager;
    }

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
            .initialCapacity(100)
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofHours(1))
            .removalListener(removalListener());
    }

    @Bean
    public RemovalListener<Object, Object> removalListener() {
        return (key, value, cause) -> {
            switch (cause) {
                // SIZE: 크기 초과로 인한 제거 (정상적인 LRU/LFU 동작)
                case SIZE:
                    log.debug("캐시 크기 초과로 인한 엔트리 제거 - key: {}", key);
                    break;
                // EXPIRED: 시간 만료로 인한 제거 (TTL 정책)
                case EXPIRED:
                    log.debug("만료 시간 도달로 인한 엔트리 제거 - key: {}", key);
                    break;
                // EXPLICIT: 수동 삭제 (@CacheEvict 등)
                case EXPLICIT:
                    log.info("수동 삭제로 인한 엔트리 제거 - key: {}", key);
                    break;
                // REPLACED: 새 값으로 교체
                case REPLACED:
                    log.debug("새 값으로 교체로 인한 엔트리 제거 - key: {}", key);
                    break;
                // 그 외 제거 원인
                default:
                    log.debug("캐시 엔트리 제거 - key: {}, cause: {}", key, cause);
            }
        };
    }

    @Bean("userIdKeyGenerator")
    public KeyGenerator notificationUserKeyGenerator() {
        return (target, method, params) -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Object principal = auth.getPrincipal();

            if (principal instanceof DiscodeitUserDetails userDetails) {
                return userDetails.getId();
            }
            return UUID.randomUUID();
        };
    }
}
