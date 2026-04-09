package com.xiaohong.lease.common.ratelimit;

/**
 * 限流类型枚举
 *
 * @author 小红
 */
public enum RateLimitType {

    /**
     * 全局限流：基于接口路径限流，所有用户共享限流配额
     */
    GLOBAL,

    /**
     * 用户限流：基于用户ID+接口路径限流，每个用户独立计算
     */
    USER,

    /**
     * IP限流：基于客户端IP+接口路径限流
     */
    IP
}
