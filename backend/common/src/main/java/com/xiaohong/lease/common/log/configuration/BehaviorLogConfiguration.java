package com.xiaohong.lease.common.log.configuration;

import com.xiaohong.lease.common.log.aspect.BehaviorLogAspect;
import com.xiaohong.lease.common.log.properties.BehaviorLogProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 用户行为日志自动配置
 *
 * @author 小红
 */
@Configuration
@EnableConfigurationProperties(BehaviorLogProperties.class)
@ConditionalOnProperty(prefix = "xiaohong.behavior-log", name = "enabled", havingValue = "true", matchIfMissing = true)
public class BehaviorLogConfiguration {

    @Bean
    public BehaviorLogAspect behaviorLogAspect(BehaviorLogProperties properties) {
        return new BehaviorLogAspect(properties);
    }
}
