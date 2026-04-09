package com.xiaohong.lease.common.mail;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * 邮件服务自动配置类
 *
 * @author 小红
 */
@Configuration
@EnableConfigurationProperties(MailProperties.class)
@ConditionalOnProperty(prefix = "spring.mail", name = "host")
public class MailConfiguration {

    /**
     * 配置JavaMailSender
     *
     * @param mailProperties 邮件配置属性
     * @return JavaMailSender实例
     */
    @Bean
    public JavaMailSender javaMailSender(MailProperties mailProperties) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailProperties.getHost());
        mailSender.setPort(mailProperties.getPort());
        mailSender.setUsername(mailProperties.getUsername());
        mailSender.setPassword(mailProperties.getPassword());
        mailSender.setDefaultEncoding(mailProperties.getEncoding());

        // 配置SMTP属性
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", mailProperties.getSsl());
        props.put("mail.smtp.ssl.enable", mailProperties.getSsl());
        props.put("mail.smtp.connectiontimeout", mailProperties.getConnectionTimeout());
        props.put("mail.smtp.timeout", mailProperties.getTimeout());
        props.put("mail.debug", mailProperties.getDebug());

        return mailSender;
    }
}
