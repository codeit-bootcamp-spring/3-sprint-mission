package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.List;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {

    private static final String CONFIG_NAME = "[CacheConfig] ";

    @Bean
    @Primary
    public CacheManager compositeCacheManager() {
        log.info(CONFIG_NAME + "compositeCacheManager 초기화 시작");
        CompositeCacheManager compositeCacheManager = new CompositeCacheManager();
        compositeCacheManager.setCacheManagers(List.of(
                channelCacheManager(),
                notificationCacheManager(),
                userCacheManager(),
                securityCacheManager()
        ));
        compositeCacheManager.setFallbackToNoOpCache(false);
        log.info(CONFIG_NAME + "compositeCacheManager 초기화 완료 - {} 개의 캐시 매니저 등록", 4);

        return compositeCacheManager;
    }

    @Bean
    public CacheManager channelCacheManager() {
        log.info(CONFIG_NAME + "channelCacheManager 초기화 시작");
        CaffeineCacheManager manager = new CaffeineCacheManager();

        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(500)                          // 채널은 공식 채널과 사설 채널이 있으므로 500개 제한
                .expireAfterWrite(Duration.ofHours(1))      // 채널 변경은 유저 변경에 비해 적어서 1시간 유지
                .recordStats()                              // 메트릭 수집 활성화
                .removalListener((key, value, cause) -> {
                    switch (cause) {
                        case SIZE -> {
                            log.warn("Channel 캐시 크기 초과로 인한 엔트리 제거 - key: {}, 현재 크기: 500", key);
                            // 성능 모니터링을 위한 경고 로그
                        }
                        case EXPIRED -> {
                            log.debug("Channel 캐시 만료로 인한 엔트리 제거 - key: {}, TTL: 1시간", key);
                        }
                        case EXPLICIT -> {
                            log.info("Channel 캐시 수동 삭제로 인한 엔트리 제거 - key: {}", key);
                        }
                        case REPLACED -> {
                            log.debug("Channel 캐시 새 값으로 교체로 인한 엔트리 제거 - key: {}", key);
                        }
                        default -> {
                            log.debug("Channel 캐시 엔트리 제거 - key: {}, cause: {}", key, cause);
                        }
                    }
                })
        );

        manager.setCacheNames(List.of(
                "channelByUser",
                "channelById"));

        log.info(CONFIG_NAME + "channelCacheManager 초기화 완료 - 캐시 이름: {}, 최대 크기: 500, TTL: 1시간",
                manager.getCacheNames());
        return manager;
    }

    @Bean
    public CacheManager notificationCacheManager() {
        log.info(CONFIG_NAME + "notificationCacheManager 초기화 시작");
        CaffeineCacheManager manager = new CaffeineCacheManager();

        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(Duration.ofMinutes(10)) // 알림은 실시간성이 중요 및 개인별 관리
                .recordStats()                              // 메트릭 수집 활성화
                .removalListener((key, value, cause) -> {
                    switch (cause) {
                        case SIZE -> {
                            log.warn("Notification 캐시 크기 초과로 인한 엔트리 제거 - key: {}, 현재 크기: 100", key);
                            // 알림 캐시 크기 초과는 사용자 경험에 영향
                        }
                        case EXPIRED -> {
                            log.debug("Notification 캐시 만료로 인한 엔트리 제거 - key: {}, TTL: 10분", key);
                        }
                        case EXPLICIT -> {
                            log.info("Notification 캐시 수동 삭제로 인한 엔트리 제거 - key: {}", key);
                        }
                        case REPLACED -> {
                            log.debug("Notification 캐시 새 값으로 교체로 인한 엔트리 제거 - key: {}", key);
                        }
                        default -> {
                            log.debug("Notification 캐시 엔트리 제거 - key: {}, cause: {}", key, cause);
                        }
                    }
                })
        );

        manager.setCacheNames(List.of("notificationByUser"));

        log.info(CONFIG_NAME + "notificationCacheManager 완료 - 캐시 이름: {}, 최대 크기: 100, TTL: 10분",
                manager.getCacheNames());
        return manager;
    }

    @Bean
    public CacheManager userCacheManager() {
        log.info(CONFIG_NAME + "userCacheManager 초기화 시작");
        CaffeineCacheManager manager = new CaffeineCacheManager();

        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)                        // 유저는 채널 수보다 많으므로
                .expireAfterWrite(Duration.ofHours(2))  // 사용자 정보 변경 빈도 고려
                .expireAfterAccess(Duration.ofMinutes(30))// 접근 기반 만료
                .recordStats()
                .removalListener((key, value, cause) -> {
                    switch (cause) {
                        case SIZE -> {
                            log.warn("User 캐시 크기 초과로 인한 엔트리 제거 - key: {}, 현재 크기: 1000", key);
                            // 사용자 캐시 크기 초과는 성능에 영향
                        }
                        case EXPIRED -> {
                            log.debug("User 캐시 만료로 인한 엔트리 제거 - key: {}, TTL: 2시간, 접근 기반: 30분", key);
                        }
                        case EXPLICIT -> {
                            log.info("User 캐시 수동 삭제로 인한 엔트리 제거 - key: {}", key);
                        }
                        case REPLACED -> {
                            log.debug("User 캐시 새 값으로 교체로 인한 엔트리 제거 - key: {}", key);
                        }
                        default -> {
                            log.debug("User 캐시 엔트리 제거 - key: {}, cause: {}", key, cause);
                        }
                    }
                })
        );

        // 사용자 목록 관련 캐시 이름들
        manager.setCacheNames(List.of(
                "users",           // 전체 사용자 목록
                "userById"           // ID로 사용자 조회
        ));

        log.info(CONFIG_NAME + "userCacheManager 초기화 완료 - 캐시 이름: {}, 최대 크기: 1000, TTL: 2시간, 접근 기반: 30분",
                manager.getCacheNames());
        return manager;
    }

    @Bean
    public CacheManager securityCacheManager() {
        log.info(CONFIG_NAME + "securityCacheManager 초기화 시작");
        CaffeineCacheManager manager = new CaffeineCacheManager();

        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(300)                           // 로그인한 유저는 전체 유저 수보다 적으므로
                .expireAfterWrite(Duration.ofHours(4))      // 로그인/로그아웃은 고려
                .expireAfterAccess(Duration.ofHours(1))     // 접근 기반 만료
                .recordStats()                              // 메트릭 수집 활성화
                .removalListener((key, value, cause) -> {
                    switch (cause) {
                        case SIZE -> {
                            log.warn("UserDetails 캐시 크기 초과로 인한 엔트리 제거 - key: {}, 현재 크기: 300", key);
                            // 보안 캐시 크기 초과는 로그인 성능에 영향
                        }
                        case EXPIRED -> {
                            log.debug("UserDetails 캐시 만료로 인한 엔트리 제거 - key: {}, TTL: 4시간, 접근 기반: 1시간", key);
                        }
                        case EXPLICIT -> {
                            log.info("UserDetails 캐시 수동 삭제로 인한 엔트리 제거 - key: {}", key);
                        }
                        case REPLACED -> {
                            log.debug("UserDetails 캐시 새 값으로 교체로 인한 엔트리 제거 - key: {}", key);
                        }
                        default -> {
                            log.debug("UserDetails 캐시 엔트리 제거 - key: {}, cause: {}", key, cause);
                        }
                    }
                })
        );

        manager.setCacheNames(List.of("userDetailsByUsername"));

        log.info(CONFIG_NAME + "securityCacheManager 초기화 완료 - 캐시 이름: {}, 최대 크기: 300, TTL: 4시간, 접근 기반: 1시간",
                manager.getCacheNames());
        return manager;
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(ObjectMapper objectMapper) {
        ObjectMapper redisObjectMapper = objectMapper.copy();
        redisObjectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.PROPERTY
        );

        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer(redisObjectMapper)
                        )
                )
                .prefixCacheNameWith("discodeit:")
                .entryTtl(Duration.ofSeconds(600))
                .disableCachingNullValues();
    }
}
