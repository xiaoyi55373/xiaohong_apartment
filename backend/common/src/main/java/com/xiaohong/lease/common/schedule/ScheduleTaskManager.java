package com.xiaohong.lease.common.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务管理器
 * 提供分布式锁功能，防止多实例同时执行定时任务
 *
 * @author 小红
 */
@Slf4j
@Component
public class ScheduleTaskManager {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ScheduleConfiguration scheduleConfiguration;

    private static final String LOCK_PREFIX = "schedule:lock:";
    private static final long LOCK_EXPIRE_SECONDS = 300; // 5分钟

    /**
     * 尝试获取分布式锁
     *
     * @param taskName 任务名称
     * @return 是否获取成功
     */
    public boolean tryLock(String taskName) {
        if (!scheduleConfiguration.isEnabled()) {
            log.info("定时任务已禁用，跳过任务: {}", taskName);
            return false;
        }

        String lockKey = LOCK_PREFIX + taskName;
        String lockValue = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, LOCK_EXPIRE_SECONDS, TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(success)) {
            log.info("获取定时任务锁成功: {}", taskName);
            return true;
        } else {
            log.debug("获取定时任务锁失败，任务正在执行: {}", taskName);
            return false;
        }
    }

    /**
     * 释放分布式锁
     *
     * @param taskName 任务名称
     */
    public void unlock(String taskName) {
        String lockKey = LOCK_PREFIX + taskName;
        redisTemplate.delete(lockKey);
        log.info("释放定时任务锁: {}", taskName);
    }

    /**
     * 延长锁的过期时间
     *
     * @param taskName 任务名称
     */
    public void extendLock(String taskName) {
        String lockKey = LOCK_PREFIX + taskName;
        redisTemplate.expire(lockKey, LOCK_EXPIRE_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 记录任务执行日志
     *
     * @param taskName 任务名称
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param result 执行结果
     * @param message 消息
     */
    public void logTaskExecution(String taskName, LocalDateTime startTime, 
                                  LocalDateTime endTime, boolean result, String message) {
        String logKey = "schedule:log:" + taskName;
        String logEntry = String.format("[%s] 开始: %s, 结束: %s, 结果: %s, 消息: %s",
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                result ? "成功" : "失败",
                message);

        redisTemplate.opsForList().leftPush(logKey, logEntry);
        redisTemplate.opsForList().trim(logKey, 0, 99); // 只保留最近100条
        
        if (result) {
            log.info("定时任务执行成功: {}, 耗时: {}ms", taskName, 
                    java.time.Duration.between(startTime, endTime).toMillis());
        } else {
            log.error("定时任务执行失败: {}, 消息: {}", taskName, message);
        }
    }

    /**
     * 获取任务最后执行时间
     *
     * @param taskName 任务名称
     * @return 最后执行时间
     */
    public LocalDateTime getLastExecutionTime(String taskName) {
        String logKey = "schedule:log:" + taskName;
        String lastLog = redisTemplate.opsForList().index(logKey, 0);
        
        if (lastLog != null && lastLog.length() > 20) {
            try {
                String timeStr = lastLog.substring(1, 20);
                return LocalDateTime.parse(timeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception e) {
                log.warn("解析任务执行时间失败: {}", lastLog);
            }
        }
        return null;
    }
}
