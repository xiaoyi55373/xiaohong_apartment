package com.xiaohong.lease.common.ratelimit.aspect;

import com.xiaohong.lease.common.constant.RedisConstant;
import com.xiaohong.lease.common.context.LoginUserContext;
import com.xiaohong.lease.common.exception.LeaseException;
import com.xiaohong.lease.common.ratelimit.RateLimitProperties;
import com.xiaohong.lease.common.ratelimit.RateLimitType;
import com.xiaohong.lease.common.ratelimit.annotation.RateLimit;
import com.xiaohong.lease.common.result.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 限流切面
 * 基于Redis滑动窗口算法实现分布式限流
 *
 * @author 小红
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "xiaohong.rate-limit", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RateLimitAspect {

    private final StringRedisTemplate stringRedisTemplate;
    private final RateLimitProperties rateLimitProperties;

    /**
     * Redis Lua脚本：滑动窗口限流
     * 1. 移除时间窗口之前的请求记录
     * 2. 统计当前窗口内的请求数
     * 3. 如果未超过限制，记录当前请求时间戳
     */
    private static final String RATE_LIMIT_LUA_SCRIPT =
            "local key = KEYS[1] " +
            "local window = tonumber(ARGV[1]) " +
            "local maxRequests = tonumber(ARGV[2]) " +
            "local now = tonumber(ARGV[3]) " +
            "local windowStart = now - window " +
            "redis.call('ZREMRANGEBYSCORE', key, 0, windowStart) " +
            "local currentRequests = redis.call('ZCARD', key) " +
            "if currentRequests < maxRequests then " +
            "    redis.call('ZADD', key, now, now) " +
            "    redis.call('EXPIRE', key, math.ceil(window / 1000)) " +
            "    return 1 " +
            "else " +
            "    return 0 " +
            "end";

    @Around("@annotation(rateLimit) || @within(rateLimit)")
    public Object around(ProceedingJoinPoint point, RateLimit rateLimit) throws Throwable {
        if (!rateLimitProperties.isEnabled()) {
            return point.proceed();
        }

        // 获取方法上的注解（方法级优先于类级）
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RateLimit methodAnnotation = method.getAnnotation(RateLimit.class);
        if (methodAnnotation != null) {
            rateLimit = methodAnnotation;
        }

        String key = buildRateLimitKey(rateLimit, method);
        int window = rateLimit.window() > 0 ? rateLimit.window() : rateLimitProperties.getDefaultWindow();
        int maxRequests = rateLimit.maxRequests() > 0 ? rateLimit.maxRequests() : rateLimitProperties.getDefaultMaxRequests();

        boolean allowed = tryAcquire(key, window, maxRequests);

        if (!allowed) {
            log.warn("请求被限流, key: {}, window: {}s, maxRequests: {}", key, window, maxRequests);
            throw new LeaseException(ResultCodeEnum.RATE_LIMIT_EXCEEDED);
        }

        return point.proceed();
    }

    /**
     * 尝试获取限流令牌
     *
     * @param key         Redis Key
     * @param window      时间窗口（秒）
     * @param maxRequests 最大请求次数
     * @return true-允许通过，false-被限流
     */
    private boolean tryAcquire(String key, int window, int maxRequests) {
        try {
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(RATE_LIMIT_LUA_SCRIPT);
            redisScript.setResultType(Long.class);

            long now = System.currentTimeMillis();
            long windowMs = window * 1000L;

            Long result = stringRedisTemplate.execute(
                    redisScript,
                    Collections.singletonList(key),
                    String.valueOf(windowMs),
                    String.valueOf(maxRequests),
                    String.valueOf(now)
            );

            return result != null && result == 1L;
        } catch (Exception e) {
            log.error("限流检查失败, key: {}", key, e);
            // Redis异常时，默认放行，避免影响正常业务
            return true;
        }
    }

    /**
     * 构建限流Key
     */
    private String buildRateLimitKey(RateLimit rateLimit, Method method) {
        String prefix = rateLimit.keyPrefix().isEmpty() ? RedisConstant.RATE_LIMIT_PREFIX : rateLimit.keyPrefix();
        String className = method.getDeclaringClass().getSimpleName();
        String methodName = method.getName();
        String baseKey = prefix + className + ":" + methodName;

        RateLimitType type = rateLimit.type();
        switch (type) {
            case USER:
                Long userId = LoginUserContext.getLoginUserId();
                return RedisConstant.USER_RATE_LIMIT_PREFIX + baseKey + ":" + (userId != null ? userId : "anonymous");
            case IP:
                String clientIp = getClientIp();
                return prefix + "ip:" + baseKey + ":" + clientIp;
            case GLOBAL:
            default:
                return baseKey;
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return "unknown";
            }
            HttpServletRequest request = attributes.getRequest();
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            // 多个代理情况，取第一个IP
            if (ip != null && ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
            return ip;
        } catch (Exception e) {
            return "unknown";
        }
    }
}
