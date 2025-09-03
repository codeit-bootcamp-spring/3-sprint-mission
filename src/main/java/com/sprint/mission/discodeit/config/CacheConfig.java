package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    @ConditionalOnProperty(name = "app.cache", havingValue = "caffeine", matchIfMissing = true)
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterAccess(Duration.ofSeconds(600))
            .recordStats();
    }

    @Bean
    @ConditionalOnProperty(name = "app.cache", havingValue = "caffeine", matchIfMissing = true)
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager("users",
            "channelsByUser", "notificationsByUser");
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    @Bean
    @ConditionalOnProperty(name = "app.cache", havingValue = "redis")
    public RedisCacheConfiguration redisCacheConfiguration(ObjectMapper objectMapper) {
        ObjectMapper redisObjectMapper = objectMapper.copy();
        redisObjectMapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            DefaultTyping.EVERYTHING,
            As.PROPERTY
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

    @Bean
    @ConditionalOnProperty(name = "app.cache", havingValue = "redis")
    public CacheManager redisCacheManager(
        RedisConnectionFactory cf, RedisCacheConfiguration cfg) {
        return RedisCacheManager.builder(cf).cacheDefaults(cfg).build();
    }

}
