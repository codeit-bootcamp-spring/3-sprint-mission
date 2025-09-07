package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.sprint.mission.discodeit.config.custom.RecordSupportingTypeResolver;
import java.time.Duration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableCaching
public class CacheConfig {

  private static final String CACHE_PREFIX = "discodeit:";
  private static final long CACHE_TTL_SECONDS = 600;

  @Bean
  public RedisCacheConfiguration redisCacheConfiguration(ObjectMapper objectMapper) {
    ObjectMapper redisObjectMapper = objectMapper.copy();
    RecordSupportingTypeResolver typeResolver =
        new RecordSupportingTypeResolver(DefaultTyping.NON_FINAL,
            redisObjectMapper.getPolymorphicTypeValidator());
    StdTypeResolverBuilder initializedResolver = typeResolver.init(JsonTypeInfo.Id.CLASS, null);
    initializedResolver = initializedResolver.inclusion(JsonTypeInfo.As.PROPERTY);
    redisObjectMapper.setDefaultTyping(initializedResolver);

    return RedisCacheConfiguration.defaultCacheConfig()
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer(redisObjectMapper)
            )
        )
        .prefixCacheNameWith(CACHE_PREFIX)
        .entryTtl(Duration.ofSeconds(CACHE_TTL_SECONDS))
        .disableCachingNullValues();
  }
}
