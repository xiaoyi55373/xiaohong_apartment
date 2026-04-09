package com.xiaohong.lease.common.circuitbreaker.annotation;

import java.lang.annotation.*;

/**
 * 熔断注解
 * 用于方法级别的熔断保护，防止服务雪崩
 *
 * @author 小红
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CircuitBreaker {

    /**
     * 熔断器名称，为空时自动生成
     */
    String name() default "";

    /**
     * 失败次数阈值，达到该值后熔断器打开
     */
    int failureThreshold() default 5;

    /**
     * 半开状态下成功次数阈值，达到该值后熔断器关闭
     */
    int successThreshold() default 3;

    /**
     * 熔断器打开状态持续时间（毫秒）
     */
    long timeoutDuration() default 30000;

    /**
     * 半开状态下允许的最大请求数
     */
    long halfOpenMaxCalls() default 3;

    /**
     * 降级方法名，需要与目标方法在同一类中，参数和返回类型兼容
     */
    String fallbackMethod() default "";
}
