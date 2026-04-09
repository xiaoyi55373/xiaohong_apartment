package com.xiaohong.lease.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 用户行为类型枚举
 *
 * @author 小红
 */
@Getter
public enum BehaviorType {

    BROWSE(1, "浏览"),
    SEARCH(2, "搜索"),
    COLLECT(3, "收藏"),
    APPOINTMENT(4, "预约看房"),
    SHARE(5, "分享"),
    LOGIN(6, "登录"),
    CLICK(7, "点击"),
    SUBMIT(8, "提交"),
    CONSULT(9, "咨询"),
    SIGN(10, "签约");

    @EnumValue
    @JsonValue
    private final Integer code;

    private final String name;

    BehaviorType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
