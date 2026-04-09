package com.xiaohong.lease.common.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 邮件配置属性类
 *
 * @author 小红
 */
@Data
@ConfigurationProperties(prefix = "spring.mail")
public class MailProperties {

    /**
     * SMTP服务器主机地址
     */
    private String host;

    /**
     * SMTP服务器端口
     */
    private Integer port = 587;

    /**
     * 发件人邮箱账号
     */
    private String username;

    /**
     * 发件人邮箱密码/授权码
     */
    private String password;

    /**
     * 发件人显示名称
     */
    private String fromName = "小红公寓";

    /**
     * 是否启用SSL/TLS加密
     */
    private Boolean ssl = true;

    /**
     * 邮件编码格式
     */
    private String encoding = "UTF-8";

    /**
     * 连接超时时间(毫秒)
     */
    private Integer connectionTimeout = 5000;

    /**
     * 发送超时时间(毫秒)
     */
    private Integer timeout = 5000;

    /**
     * 调试模式
     */
    private Boolean debug = false;
}
