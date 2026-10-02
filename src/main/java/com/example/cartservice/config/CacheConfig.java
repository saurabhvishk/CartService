package com.example.cartservice.config;

import com.example.cartservice.dtos.CartDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.LoggingCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    public static final String CARTS_CACHE = "carts";

    // store carts in Redis as readable JSON, and forget them after a while
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(ObjectMapper objectMapper,
                                                           @Value("${cart.cache.ttl-minutes}") long ttlMinutes) {
        Jackson2JsonRedisSerializer<CartDto> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, CartDto.class);
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(ttlMinutes))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }

    // if Redis is down, log it and fall back to MongoDB instead of failing the request
    @Override
    public CacheErrorHandler errorHandler() {
        return new LoggingCacheErrorHandler();
    }
}
