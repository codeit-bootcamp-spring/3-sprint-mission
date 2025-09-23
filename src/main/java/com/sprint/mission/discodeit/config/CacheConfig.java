package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory,
                                     RedisCacheConfiguration redisCacheConfiguration) {
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }

    @Bean
    public CaffeineCacheManager caffeineFallbackCacheManager() {

        CaffeineCacheManager manager = new CaffeineCacheManager();

        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(Duration.ofMinutes(10))
                .expireAfterAccess(Duration.ofMinutes(10))
                .recordStats()
                .removalListener((key, value, cause) -> {
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
                })
        );

        manager.setCacheNames(List.of("users", "userChannels", "userNotifications"));
        return manager;
    }

    @Bean("userIdKeyGenerator")
    public KeyGenerator userIdKeyGenerator(UserRepository userRepository) {
        return (target, method, params) -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            return userRepository.findByUsername(username)
                    .map(User::getId)
                    .orElseThrow(() -> new UserNotFoundException(username));
        };
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
