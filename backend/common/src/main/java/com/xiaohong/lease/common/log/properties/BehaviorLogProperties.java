package com.xiaohong.lease.common.log.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户行为日志配置属性
 *
 * @author 小红
 */
@Data
@ConfigurationProperties(prefix = "xiaohong.behavior-log")
public class BehaviorLogProperties {

    /**
     * 是否启用行为日志
     */
    private boolean enabled = true;

    /**
     * 请求参数最大长度
     */
    private int paramMaxLength = 2000;

    /**
     * 忽略的URL前缀列表
     */
    private List<String> ignoreUrls = new ArrayList<>();

    /**
     * 忽略的参数列表
     */
    private List<String> ignoreParams = List.of("password", "token", "secret", "captcha");
}
