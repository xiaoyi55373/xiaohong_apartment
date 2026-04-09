package com.xiaohong.lease.common.ratelimit;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 限流配置属性
 *
 * @author 小红
 */
@Data
@ConfigurationProperties(prefix = "xiaohong.rate-limit")
public class RateLimitProperties {

    /**
     * 是否启用限流
     */
    private boolean enabled = true;

    /**
     * 默认时间窗口（秒）
     */
    private int defaultWindow = 60;

    /**
     * 默认最大请求次数
     */
    private int defaultMaxRequests = 100;

    /**
     * 登录接口限流窗口（秒）
     */
    private int loginWindow = 60;

    /**
     * 登录接口最大请求次数
     */
    private int loginMaxRequests = 5;

    /**
     * 短信发送接口限流窗口（秒）
     */
    private int smsWindow = 60;

    /**
     * 短信发送接口最大请求次数
     */
    private int smsMaxRequests = 3;
}
