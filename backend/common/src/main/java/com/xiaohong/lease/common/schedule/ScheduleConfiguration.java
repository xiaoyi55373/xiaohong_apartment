package com.xiaohong.lease.common.schedule;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 定时任务配置类
 *
 * @author 小红
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "schedule")
public class ScheduleConfiguration {

    /**
     * 是否启用定时任务
     */
    private boolean enabled = true;

    /**
     * 租约到期提醒配置
     */
    private LeaseReminderConfig leaseReminder = new LeaseReminderConfig();

    /**
     * 预约过期处理配置
     */
    private AppointmentExpireConfig appointmentExpire = new AppointmentExpireConfig();

    /**
     * 数据统计配置
     */
    private StatisticsConfig statistics = new StatisticsConfig();

    /**
     * 数据清理配置
     */
    private DataCleanupConfig dataCleanup = new DataCleanupConfig();

    /**
     * 租约到期提醒配置
     */
    @Data
    public static class LeaseReminderConfig {
        /**
         * 是否启用
         */
        private boolean enabled = true;

        /**
         * 提前提醒天数（默认7天、3天、1天）
         */
        private int[] reminderDays = {7, 3, 1};

        /**
         * 提醒时间（HH:mm格式）
         */
        private String reminderTime = "09:00";

        /**
         * 是否发送短信
         */
        private boolean sendSms = true;

        /**
         * 是否发送邮件
         */
        private boolean sendEmail = true;
    }

    /**
     * 预约过期处理配置
     */
    @Data
    public static class AppointmentExpireConfig {
        /**
         * 是否启用
         */
        private boolean enabled = true;

        /**
         * 预约过期时间（小时，超过此时间未看房的预约自动标记为过期）
         */
        private int expireHours = 24;

        /**
         * 处理时间（HH:mm格式）
         */
        private String processTime = "23:00";
    }

    /**
     * 数据统计配置
     */
    @Data
    public static class StatisticsConfig {
        /**
         * 是否启用
         */
        private boolean enabled = true;

        /**
         * 统计时间（HH:mm格式）
         */
        private String statisticsTime = "01:00";

        /**
         * 保留天数统计的天数
         */
        private int retentionDays = 30;
    }

    /**
     * 数据清理配置
     */
    @Data
    public static class DataCleanupConfig {
        /**
         * 是否启用
         */
        private boolean enabled = true;

        /**
         * 清理时间（HH:mm格式）
         */
        private String cleanupTime = "02:00";

        /**
         * 操作日志保留天数
         */
        private int operationLogRetentionDays = 180;

        /**
         * 登录日志保留天数
         */
        private int loginLogRetentionDays = 90;

        /**
         * 行为日志保留天数
         */
        private int behaviorLogRetentionDays = 30;

        /**
         * 临时文件保留天数
         */
        private int tempFileRetentionDays = 7;
    }
}
