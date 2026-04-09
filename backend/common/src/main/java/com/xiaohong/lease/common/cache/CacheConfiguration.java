package com.xiaohong.lease.common.cache;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis缓存配置类
 * 配置Spring Cache和RedisTemplate
 * 
 * @author 小红
 */
@Slf4j
@Configuration
@EnableCaching
@ConditionalOnProperty(prefix = "xiaohong.cache", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CacheConfiguration implements CachingConfigurer {

    /**
     * 默认缓存过期时间（分钟）
     */
    private static final long DEFAULT_CACHE_EXPIRE_MINUTES = 30;

    /**
     * 配置RedisTemplate
     * 使用JSON序列化方式存储对象
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 配置ObjectMapper，启用类型信息
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        // 使用GenericJackson2JsonRedisSerializer作为值的序列化器
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // Key序列化器使用StringRedisSerializer
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        log.info("RedisTemplate配置完成，使用JSON序列化");
        return template;
    }

    /**
     * 配置CacheManager
     * 支持多个缓存区域，每个区域可以有不同的过期时间
     */
    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 默认缓存配置
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(DEFAULT_CACHE_EXPIRE_MINUTES))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        // 配置不同缓存区域的过期时间
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // 公寓相关缓存 - 30分钟
        cacheConfigurations.put("apartment", defaultConfig.entryTtl(Duration.ofMinutes(30)));

        // 房间相关缓存 - 30分钟
        cacheConfigurations.put("room", defaultConfig.entryTtl(Duration.ofMinutes(30)));

        // 地区相关缓存 - 1小时（变化较少）
        cacheConfigurations.put("region", defaultConfig.entryTtl(Duration.ofHours(1)));

        // 配置相关缓存 - 2小时（几乎不变）
        cacheConfigurations.put("config", defaultConfig.entryTtl(Duration.ofHours(2)));

        // 用户相关缓存 - 30分钟
        cacheConfigurations.put("user", defaultConfig.entryTtl(Duration.ofMinutes(30)));

        // 租约相关缓存 - 30分钟
        cacheConfigurations.put("agreement", defaultConfig.entryTtl(Duration.ofMinutes(30)));

        // 预约相关缓存 - 20分钟
        cacheConfigurations.put("appointment", defaultConfig.entryTtl(Duration.ofMinutes(20)));

        // 系统相关缓存 - 1小时
        cacheConfigurations.put("system", defaultConfig.entryTtl(Duration.ofHours(1)));

        // 热点数据缓存 - 10分钟
        cacheConfigurations.put("hot", defaultConfig.entryTtl(Duration.ofMinutes(10)));

        RedisCacheManager cacheManager = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();

        log.info("CacheManager配置完成，默认过期时间: {}分钟", DEFAULT_CACHE_EXPIRE_MINUTES);
        return cacheManager;
    }

    /**
     * 自定义Key生成器
     * 生成格式: 类名:方法名:参数1:参数2:...
     */
    @Bean("customKeyGenerator")
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder key = new StringBuilder();
            key.append(target.getClass().getSimpleName())
               .append(CacheConstant.CACHE_KEY_SEPARATOR)
               .append(method.getName());
            
            for (Object param : params) {
                key.append(CacheConstant.CACHE_KEY_SEPARATOR);
                if (param != null) {
                    key.append(param.toString());
                } else {
                    key.append("null");
                }
            }
            return key.toString();
        };
    }

    /**
     * 简单Key生成器（仅使用方法名和参数）
     */
    @Bean("simpleKeyGenerator")
    public KeyGenerator simpleKeyGenerator() {
        return (target, method, params) -> {
            StringBuilder key = new StringBuilder(method.getName());
            for (Object param : params) {
                key.append(CacheConstant.CACHE_KEY_SEPARATOR);
                if (param != null) {
                    key.append(param.toString());
                } else {
                    key.append("null");
                }
            }
            return key.toString();
        };
    }
}
