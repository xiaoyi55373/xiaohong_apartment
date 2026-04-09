package com.xiaohong.lease.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 操作者类型枚举
 *
 * @author 小红
 */
@Getter
public enum OperatorType {

    OTHER(0, "其他"),
    SYSTEM_USER(1, "后台用户"),
    APP_USER(2, "移动端用户"),
    SYSTEM(3, "系统");

    @EnumValue
    @JsonValue
    private final Integer code;

    private final String name;

    OperatorType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
