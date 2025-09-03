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

    @Primary
    @Bean
    public CacheManager compositeCacheManager() {
        CompositeCacheManager composite = new CompositeCacheManager();

        composite.setCacheManagers(
                List.of(
                        channelCacheManager(),
                        notificationCacheManager(),
                        userCacheManager()
                )
        );
        composite.setFallbackToNoOpCache(false);

        return composite;
    }

    @Bean
    public CacheManager channelCacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager();

        manager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .expireAfterWrite(Duration.ofMinutes(10))
                        .expireAfterAccess(Duration.ofMinutes(10))
                        .recordStats()
                        .removalListener((key, value, cause) ->{
                                switch (cause) {
                                    // SIZE: 캐시 크기 초과로 인한 제거 (정상적인 LRU, LFU의 동작)
                                    case SIZE -> {
                                        log.debug("캐시 크기 초과로 인한 엔트리 제거 - key: {}", key);
                                        break;
                                    }
                                    // EXPIRED: 시간 만료로 인한 제거 (TTL 정책)
                                    case EXPIRED -> {
                                        log.debug("만료 시간 도달로 인한 엔트리 제거 - key: {}", key);
                                        break;
                                    }
                                    // EXPLICIT: 수동 삭제 (메뉴 삭제용 메서드에 정의된 @CacheEvict 등)
                                    case EXPLICIT -> {
                                        log.debug("수동 삭제로 인한 엔트리 제거 - key: {}", key);
                                        break;
                                    }
                                    // REPLACED: 새 값으로 교체 (메뉴 수정 등)
                                    case REPLACED -> {
                                        log.debug("새 값으로 교체로 인한 엔트리 제거 - key: {}", key);
                                        break;
                                    }
                                    // 그 외 제거 원인
                                    default -> log.debug("캐시 엔트리 제거 - key: {},cause: {}", key, cause);
                                }
                        })
        );

        manager.setCacheNames(List.of("channels","channelById"));
        return manager;
    }

    @Bean
    public CacheManager notificationCacheManager() {

        CaffeineCacheManager manager = new CaffeineCacheManager();

        manager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(1000)
                        .expireAfterWrite(Duration.ofMinutes(1))
                        .expireAfterAccess(Duration.ofMinutes(1))
                        .recordStats()
                        .removalListener((key, value, cause) ->{
                            switch (cause) {
                                case SIZE: log.debug("Category 캐시 크기 초과로 인한 엔트리 제거 - key: {}", key); break;
                                case EXPIRED: log.debug("Category 캐시 만료로 인한 엔트리 제거 - key: {}", key); break;
                                case EXPLICIT: log.info("Category 캐시 수동 삭제로 인한 엔트리 제거 - key: {}", key); break;
                                case REPLACED: log.debug("Category 캐시 새 값으로 교체로 인한 엔트리 제거 - key: {}", key); break;
                                default: log.debug("Category 캐시 엔트리 제거 - key: {}, cause: {}", key, cause);
                            }
                        })
        );
        manager.setCacheNames(List.of("notifications"));
        return manager;
    }

    @Bean
    public CacheManager userCacheManager(){
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(1000)
                        .expireAfterWrite(Duration.ofMinutes(1))
                        .expireAfterAccess(Duration.ofMinutes(1))
                        .recordStats()
                        .removalListener((key, value, cause) ->{
                            switch (cause) {
                                case SIZE: log.debug("Category 캐시 크기 초과로 인한 엔트리 제거 - key: {}", key); break;
                                case EXPIRED: log.debug("Category 캐시 만료로 인한 엔트리 제거 - key: {}", key); break;
                                case EXPLICIT: log.info("Category 캐시 수동 삭제로 인한 엔트리 제거 - key: {}", key); break;
                                case REPLACED: log.debug("Category 캐시 새 값으로 교체로 인한 엔트리 제거 - key: {}", key); break;
                                default: log.debug("Category 캐시 엔트리 제거 - key: {}, cause: {}", key, cause);
                            }
                        })
        );
        manager.setCacheNames(List.of("userById","users"));
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
