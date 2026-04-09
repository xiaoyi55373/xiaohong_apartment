package com.xiaohong.lease.common.operationlog.configuration;

import com.xiaohong.lease.common.operationlog.aspect.OperationLogAspect;
import com.xiaohong.lease.common.operationlog.properties.OperationLogProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 操作日志自动配置类
 *
 * @author 小红
 */
@Configuration
@EnableConfigurationProperties(OperationLogProperties.class)
@ConditionalOnProperty(prefix = "xiaohong.operation-log", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OperationLogConfiguration {

    @Bean
    public OperationLogAspect operationLogAspect(OperationLogProperties properties, ApplicationEventPublisher eventPublisher) {
        return new OperationLogAspect(properties, eventPublisher);
    }
}
