package com.xiaohong.lease.common.operationlog.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作日志配置属性
 *
 * @author 小红
 */
@Data
@ConfigurationProperties(prefix = "xiaohong.operation-log")
public class OperationLogProperties {

    /**
     * 是否启用操作日志
     */
    private boolean enabled = true;

    /**
     * 是否异步保存日志
     */
    private boolean async = true;

    /**
     * 请求参数最大长度
     */
    private int paramMaxLength = 2000;

    /**
     * 响应结果最大长度
     */
    private int resultMaxLength = 2000;

    /**
     * 忽略的URL前缀列表
     */
    private List<String> ignoreUrls = new ArrayList<>();

    /**
     * 忽略的参数名列表（包含这些关键词的参数不记录）
     */
    private List<String> ignoreParams = new ArrayList<>();

    public OperationLogProperties() {
        // 默认忽略的URL
        ignoreUrls.add("/admin/system/operationLog");
        ignoreUrls.add("/admin/system/loginLog");
        ignoreUrls.add("/admin/monitor");
        
        // 默认忽略的参数（敏感信息）
        ignoreParams.add("password");
        ignoreParams.add("passwd");
        ignoreParams.add("pwd");
        ignoreParams.add("secret");
        ignoreParams.add("token");
        ignoreParams.add("key");
        ignoreParams.add("credential");
        ignoreParams.add("credit");
        ignoreParams.add("card");
    }
}
