package com.xiaohong.lease.common.ratelimit.annotation;

import com.xiaohong.lease.common.ratelimit.RateLimitType;

import java.lang.annotation.*;

/**
 * 限流注解
 * 用于接口级别的流量控制，支持全局、用户、IP三种限流模式
 *
 * @author 小红
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 限流类型
     */
    RateLimitType type() default RateLimitType.GLOBAL;

    /**
     * 时间窗口（秒）
     */
    int window() default 60;

    /**
     * 时间窗口内允许的最大请求次数
     */
    int maxRequests() default 100;

    /**
     * 限流提示消息
     */
    String message() default "请求过于频繁，请稍后再试";

    /**
     * 限流Key前缀，用于自定义Redis Key
     */
    String keyPrefix() default "";
}
