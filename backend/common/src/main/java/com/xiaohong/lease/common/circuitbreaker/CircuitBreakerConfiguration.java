package com.xiaohong.lease.common.circuitbreaker;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 熔断器自动配置类
 *
 * @author 小红
 */
@Configuration
@EnableConfigurationProperties(CircuitBreakerProperties.class)
@ConditionalOnProperty(prefix = "xiaohong.circuit-breaker", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CircuitBreakerConfiguration {
}
