package com.sprint.mission.discodeit.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.sprint.mission.discodeit.config.custom.RecordSupportingTypeResolver;

@Configuration
public class RedisConfig {

  @Bean
  public RedisTemplate<String, Object> redisTemplate(
      RedisConnectionFactory connectionFactory,
      @Qualifier("redisSerializer") GenericJackson2JsonRedisSerializer redisSerializer) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(redisSerializer);
    template.setHashValueSerializer(redisSerializer);
    template.afterPropertiesSet();
    return template;
  }

  @Bean("redisSerializer")
  public GenericJackson2JsonRedisSerializer redisSerializer(ObjectMapper objectMapper) {
    ObjectMapper redisObjectMapper = objectMapper.copy();
    RecordSupportingTypeResolver typeResolver =
        new RecordSupportingTypeResolver(DefaultTyping.NON_FINAL,
            redisObjectMapper.getPolymorphicTypeValidator());
    StdTypeResolverBuilder initializedResolver = typeResolver.init(JsonTypeInfo.Id.CLASS, null);
    initializedResolver = initializedResolver.inclusion(JsonTypeInfo.As.PROPERTY);
    redisObjectMapper.setDefaultTyping(initializedResolver);
    return new GenericJackson2JsonRedisSerializer(redisObjectMapper);
  }
}
