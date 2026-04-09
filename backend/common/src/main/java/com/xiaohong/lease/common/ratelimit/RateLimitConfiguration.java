package com.xiaohong.lease.common.ratelimit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 限流自动配置类
 *
 * @author 小红
 */
@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
@ConditionalOnProperty(prefix = "xiaohong.rate-limit", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RateLimitConfiguration {
}
