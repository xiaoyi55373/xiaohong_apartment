package com.xiaohong.lease.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 定时任务类型枚举
 *
 * @author 小红
 */
public enum TaskType implements BaseEnum {

    LEASE_EXPIRE_REMIND("LEASE_EXPIRE_REMIND", "租约到期提醒", "检查即将到期的租约并发送提醒通知"),
    APPOINTMENT_EXPIRE("APPOINTMENT_EXPIRE", "预约过期处理", "自动处理过期的预约记录"),
    DATA_CLEANUP("DATA_CLEANUP", "数据清理", "清理过期数据，如操作日志、登录日志等"),
    BROWSING_HISTORY_CLEANUP("BROWSING_HISTORY_CLEANUP", "浏览历史清理", "清理过期的浏览历史记录"),
    CACHE_WARMUP("CACHE_WARMUP", "缓存预热", "预热热点数据到缓存"),
    STATISTICS_CALCULATION("STATISTICS_CALCULATION", "统计计算", "计算日/周/月统计数据");

    @EnumValue
    @JsonValue
    private String code;

    private String name;

    private String description;

    TaskType(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    @Override
    public Integer getCode() {
        return null; // String类型code，返回null
    }

    public String getStringCode() {
        return this.code;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    /**
     * 根据code获取枚举
     */
    public static TaskType getByCode(String code) {
        for (TaskType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
