package com.xiaohong.lease.common.circuitbreaker;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 熔断配置属性
 *
 * @author 小红
 */
@Data
@ConfigurationProperties(prefix = "xiaohong.circuit-breaker")
public class CircuitBreakerProperties {

    /**
     * 是否启用熔断
     */
    private boolean enabled = true;

    /**
     * 默认失败次数阈值
     */
    private int defaultFailureThreshold = 5;

    /**
     * 默认成功次数阈值
     */
    private int defaultSuccessThreshold = 3;

    /**
     * 默认熔断持续时间（毫秒）
     */
    private long defaultTimeoutDuration = 30000;

    /**
     * 默认半开状态最大请求数
     */
    private int defaultHalfOpenMaxCalls = 3;
}
