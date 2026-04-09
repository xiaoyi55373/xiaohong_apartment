package com.xiaohong.lease.common.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 缓存服务类
 * 提供统一的缓存操作方法，支持缓存穿透防护、缓存预热等功能
 * 
 * @author 小红
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 空值缓存时间（分钟）- 用于防止缓存穿透
     */
    private static final long NULL_VALUE_EXPIRE_MINUTES = 5;

    /**
     * 空值标记
     */
    private static final String NULL_VALUE = "CACHE_NULL";

    /**
     * 获取缓存
     * 
     * @param key 缓存Key
     * @return 缓存值，不存在返回null
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (NULL_VALUE.equals(value)) {
                return null; // 空值标记，表示数据库中不存在
            }
            return (T) value;
        } catch (Exception e) {
            log.error("获取缓存失败, key: {}", key, e);
            return null;
        }
    }

    /**
     * 设置缓存
     * 
     * @param key     缓存Key
     * @param value   缓存值
     * @param minutes 过期时间（分钟）
     */
    public void set(String key, Object value, long minutes) {
        try {
            if (value == null) {
                // 缓存空值，防止缓存穿透
                redisTemplate.opsForValue().set(key, NULL_VALUE, NULL_VALUE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            } else {
                redisTemplate.opsForValue().set(key, value, minutes, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.error("设置缓存失败, key: {}", key, e);
        }
    }

    /**
     * 设置缓存（带随机过期时间，防止缓存雪崩）
     * 
     * @param key          缓存Key
     * @param value        缓存值
     * @param minutes      基础过期时间（分钟）
     * @param randomMinutes 随机偏移时间（分钟）
     */
    public void setWithRandomExpire(String key, Object value, long minutes, long randomMinutes) {
        try {
            long expireMinutes = minutes + (long) (Math.random() * randomMinutes);
            set(key, value, expireMinutes);
        } catch (Exception e) {
            log.error("设置缓存失败, key: {}", key, e);
        }
    }

    /**
     * 删除缓存
     * 
     * @param key 缓存Key
     */
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("删除缓存, key: {}", key);
        } catch (Exception e) {
            log.error("删除缓存失败, key: {}", key, e);
        }
    }

    /**
     * 批量删除缓存
     * 
     * @param keys 缓存Key集合
     */
    public void delete(Collection<String> keys) {
        try {
            redisTemplate.delete(keys);
            log.debug("批量删除缓存, keys: {}", keys);
        } catch (Exception e) {
            log.error("批量删除缓存失败, keys: {}", keys, e);
        }
    }

    /**
     * 根据模式删除缓存
     * 
     * @param pattern Key模式，如 "apartment:*"
     */
    public void deleteByPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.debug("根据模式删除缓存, pattern: {}, 删除数量: {}", pattern, keys.size());
            }
        } catch (Exception e) {
            log.error("根据模式删除缓存失败, pattern: {}", pattern, e);
        }
    }

    /**
     * 判断缓存是否存在
     * 
     * @param key 缓存Key
     * @return true-存在，false-不存在
     */
    public boolean hasKey(String key) {
        try {
            Boolean hasKey = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(hasKey);
        } catch (Exception e) {
            log.error("判断缓存是否存在失败, key: {}", key, e);
            return false;
        }
    }

    /**
     * 获取缓存过期时间
     * 
     * @param key 缓存Key
     * @return 过期时间（秒），-1表示永不过期，-2表示不存在
     */
    public Long getExpire(String key) {
        try {
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("获取缓存过期时间失败, key: {}", key, e);
            return -2L;
        }
    }

    /**
     * 设置缓存过期时间
     * 
     * @param key     缓存Key
     * @param minutes 过期时间（分钟）
     * @return true-设置成功，false-设置失败
     */
    public boolean expire(String key, long minutes) {
        try {
            Boolean result = redisTemplate.expire(key, minutes, TimeUnit.MINUTES);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("设置缓存过期时间失败, key: {}", key, e);
            return false;
        }
    }

    /**
     * 缓存获取或加载（带缓存穿透防护）
     * 如果缓存不存在，从数据库加载并放入缓存
     * 
     * @param key       缓存Key
     * @param minutes   过期时间（分钟）
     * @param loader    数据加载器
     * @return 缓存值
     */
    public <T> T getOrLoad(String key, long minutes, Supplier<T> loader) {
        // 先尝试从缓存获取
        T value = get(key);
        if (value != null) {
            return value;
        }

        // 检查是否是空值标记（缓存穿透防护）
        Object cachedValue = redisTemplate.opsForValue().get(key);
        if (NULL_VALUE.equals(cachedValue)) {
            return null;
        }

        // 从数据库加载
        try {
            value = loader.get();
            // 放入缓存（空值也会缓存，防止缓存穿透）
            set(key, value, minutes);
            return value;
        } catch (Exception e) {
            log.error("加载数据失败, key: {}", key, e);
            return null;
        }
    }

    /**
     * 缓存获取或加载（带随机过期时间）
     * 
     * @param key           缓存Key
     * @param minutes       基础过期时间（分钟）
     * @param randomMinutes 随机偏移时间（分钟）
     * @param loader        数据加载器
     * @return 缓存值
     */
    public <T> T getOrLoadWithRandomExpire(String key, long minutes, long randomMinutes, Supplier<T> loader) {
        T value = get(key);
        if (value != null) {
            return value;
        }

        Object cachedValue = redisTemplate.opsForValue().get(key);
        if (NULL_VALUE.equals(cachedValue)) {
            return null;
        }

        try {
            value = loader.get();
            setWithRandomExpire(key, value, minutes, randomMinutes);
            return value;
        } catch (Exception e) {
            log.error("加载数据失败, key: {}", key, e);
            return null;
        }
    }

    /**
     * 递增操作
     * 
     * @param key   缓存Key
     * @param delta 增量
     * @return 递增后的值
     */
    public Long increment(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("缓存递增操作失败, key: {}", key, e);
            return null;
        }
    }

    /**
     * 获取所有匹配的Key
     * 
     * @param pattern 匹配模式，如 "apartment:*"
     * @return Key集合
     */
    public Set<String> keys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            log.error("获取缓存Key失败, pattern: {}", pattern, e);
            return null;
        }
    }

    /**
     * 清空所有缓存
     */
    public void clearAll() {
        try {
            Set<String> keys = redisTemplate.keys(CacheConstant.CACHE_KEY_PREFIX + ":*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("清空所有缓存，删除数量: {}", keys.size());
            }
        } catch (Exception e) {
            log.error("清空所有缓存失败", e);
        }
    }
}
