package com.xiaohong.lease.common.circuitbreaker;

/**
 * 熔断器状态枚举
 *
 * @author 小红
 */
public enum CircuitBreakerState {

    /**
     * 关闭状态：正常处理请求
     */
    CLOSED,

    /**
     * 打开状态：拒绝所有请求，直接返回降级结果
     */
    OPEN,

    /**
     * 半开状态：允许部分请求通过，用于探测服务是否恢复
     */
    HALF_OPEN
}
