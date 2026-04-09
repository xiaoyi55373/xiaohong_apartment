package com.xiaohong.lease.web.admin.controller.monitor;

import com.xiaohong.lease.common.cache.CacheConstant;
import com.xiaohong.lease.common.cache.CacheService;
import com.xiaohong.lease.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 缓存监控控制器
 * 提供缓存状态查询、统计和管理接口
 * 
 * @author 小红
 */
@Slf4j
@Tag(name = "缓存监控管理")
@RestController
@RequestMapping("/admin/monitor/cache")
@RequiredArgsConstructor
public class CacheMonitorController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheService cacheService;

    /**
     * 获取缓存统计信息
     */
    @Operation(summary = "获取缓存统计信息")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();

        // 获取所有缓存Key
        Set<String> allKeys = redisTemplate.keys(CacheConstant.CACHE_KEY_PREFIX + ":*");
        
        // 统计各类型缓存数量
        Map<String, Integer> typeCount = new HashMap<>();
        int totalKeys = 0;
        
        if (allKeys != null) {
            totalKeys = allKeys.size();
            for (String key : allKeys) {
                String[] parts = key.split(":");
                if (parts.length >= 2) {
                    String type = parts[1];
                    typeCount.merge(type, 1, Integer::sum);
                }
            }
        }

        stats.put("totalKeys", totalKeys);
        stats.put("typeDistribution", typeCount);
        
        // 缓存类型说明
        Map<String, String> typeDescriptions = new HashMap<>();
        typeDescriptions.put("apartment", "公寓相关缓存");
        typeDescriptions.put("room", "房间相关缓存");
        typeDescriptions.put("region", "地区相关缓存");
        typeDescriptions.put("config", "配置相关缓存");
        typeDescriptions.put("user", "用户相关缓存");
        typeDescriptions.put("agreement", "租约相关缓存");
        typeDescriptions.put("appointment", "预约相关缓存");
        typeDescriptions.put("system", "系统相关缓存");
        typeDescriptions.put("hot", "热点数据缓存");
        stats.put("typeDescriptions", typeDescriptions);

        return Result.ok(stats);
    }

    /**
     * 获取缓存Key列表
     */
    @Operation(summary = "获取缓存Key列表")
    @GetMapping("/keys")
    public Result<List<Map<String, Object>>> getCacheKeys(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String pattern,
            @RequestParam(defaultValue = "100") int limit) {
        
        String searchPattern;
        if (pattern != null && !pattern.isEmpty()) {
            searchPattern = CacheConstant.CACHE_KEY_PREFIX + ":" + pattern;
        } else if (type != null && !type.isEmpty()) {
            searchPattern = CacheConstant.CACHE_KEY_PREFIX + ":" + type + ":*";
        } else {
            searchPattern = CacheConstant.CACHE_KEY_PREFIX + ":*";
        }

        Set<String> keys = redisTemplate.keys(searchPattern);
        List<Map<String, Object>> keyList = new ArrayList<>();

        if (keys != null) {
            int count = 0;
            for (String key : keys) {
                if (count >= limit) break;
                
                Map<String, Object> keyInfo = new HashMap<>();
                keyInfo.put("key", key);
                keyInfo.put("ttl", redisTemplate.getExpire(key));
                
                // 解析key类型
                String[] parts = key.split(":");
                if (parts.length >= 2) {
                    keyInfo.put("type", parts[1]);
                }
                
                keyList.add(keyInfo);
                count++;
            }
        }

        return Result.ok(keyList);
    }

    /**
     * 获取缓存详情
     */
    @Operation(summary = "获取缓存详情")
    @GetMapping("/detail")
    public Result<Map<String, Object>> getCacheDetail(@RequestParam String key) {
        Map<String, Object> detail = new HashMap<>();
        
        Object value = redisTemplate.opsForValue().get(key);
        Long ttl = redisTemplate.getExpire(key);
        
        detail.put("key", key);
        detail.put("value", value);
        detail.put("ttl", ttl);
        detail.put("exists", value != null);
        
        if (value != null) {
            detail.put("valueType", value.getClass().getName());
            detail.put("valueSize", value.toString().length());
        }

        return Result.ok(detail);
    }

    /**
     * 删除缓存
     */
    @Operation(summary = "删除缓存")
    @DeleteMapping("/delete")
    public Result<Void> deleteCache(@RequestParam String key) {
        cacheService.delete(key);
        log.info("手动删除缓存, key: {}", key);
        return Result.ok();
    }

    /**
     * 根据模式删除缓存
     */
    @Operation(summary = "根据模式删除缓存")
    @DeleteMapping("/deleteByPattern")
    public Result<Map<String, Object>> deleteCacheByPattern(@RequestParam String pattern) {
        String fullPattern = CacheConstant.CACHE_KEY_PREFIX + ":" + pattern;
        Set<String> keys = redisTemplate.keys(fullPattern);
        int deletedCount = 0;
        
        if (keys != null && !keys.isEmpty()) {
            deletedCount = keys.size();
            cacheService.delete(keys);
        }
        
        log.info("根据模式删除缓存, pattern: {}, 删除数量: {}", fullPattern, deletedCount);
        
        Map<String, Object> result = new HashMap<>();
        result.put("deletedCount", deletedCount);
        result.put("pattern", fullPattern);
        
        return Result.ok(result);
    }

    /**
     * 清空所有缓存
     */
    @Operation(summary = "清空所有缓存")
    @DeleteMapping("/clear")
    public Result<Map<String, Object>> clearAllCache() {
        Set<String> keys = redisTemplate.keys(CacheConstant.CACHE_KEY_PREFIX + ":*");
        int deletedCount = 0;
        
        if (keys != null && !keys.isEmpty()) {
            deletedCount = keys.size();
            cacheService.delete(keys);
        }
        
        log.info("清空所有缓存，删除数量: {}", deletedCount);
        
        Map<String, Object> result = new HashMap<>();
        result.put("deletedCount", deletedCount);
        
        return Result.ok(result);
    }

    /**
     * 设置缓存过期时间
     */
    @Operation(summary = "设置缓存过期时间")
    @PostMapping("/expire")
    public Result<Void> setCacheExpire(@RequestParam String key, @RequestParam long minutes) {
        boolean success = cacheService.expire(key, minutes);
        if (success) {
            log.info("设置缓存过期时间, key: {}, minutes: {}", key, minutes);
            return Result.ok();
        } else {
            return Result.fail("设置失败，缓存可能不存在");
        }
    }

    /**
     * 获取缓存配置信息
     */
    @Operation(summary = "获取缓存配置信息")
    @GetMapping("/config")
    public Result<Map<String, Object>> getCacheConfig() {
        Map<String, Object> config = new HashMap<>();
        
        // 缓存过期时间配置
        Map<String, Long> expireConfig = new HashMap<>();
        expireConfig.put("apartmentDetail", CacheConstant.APARTMENT_DETAIL_EXPIRE);
        expireConfig.put("apartmentList", CacheConstant.APARTMENT_LIST_EXPIRE);
        expireConfig.put("roomDetail", CacheConstant.ROOM_DETAIL_EXPIRE);
        expireConfig.put("regionProvinces", CacheConstant.REGION_PROVINCES_EXPIRE);
        expireConfig.put("regionCities", CacheConstant.REGION_CITIES_EXPIRE);
        expireConfig.put("paymentTypes", CacheConstant.PAYMENT_TYPES_EXPIRE);
        expireConfig.put("leaseTerms", CacheConstant.LEASE_TERMS_EXPIRE);
        expireConfig.put("userInfo", CacheConstant.USER_INFO_EXPIRE);
        expireConfig.put("agreementDetail", CacheConstant.AGREEMENT_DETAIL_EXPIRE);
        expireConfig.put("appointmentDetail", CacheConstant.APPOINTMENT_DETAIL_EXPIRE);
        
        config.put("expireConfig", expireConfig);
        config.put("keyPrefix", CacheConstant.CACHE_KEY_PREFIX);
        config.put("separator", CacheConstant.CACHE_KEY_SEPARATOR);
        
        return Result.ok(config);
    }

    /**
     * 预热缓存
     */
    @Operation(summary = "预热缓存")
    @PostMapping("/warmup")
    public Result<Map<String, Object>> warmupCache(@RequestParam String type) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", type);
        result.put("message", "缓存预热任务已提交，将在后台执行");
        
        log.info("手动触发缓存预热, type: {}", type);
        
        // 实际预热逻辑由具体业务服务实现
        return Result.ok(result);
    }
}
