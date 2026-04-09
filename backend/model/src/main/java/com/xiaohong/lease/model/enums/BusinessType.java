package com.xiaohong.lease.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 业务操作类型枚举
 *
 * @author 小红
 */
@Getter
public enum BusinessType {

    OTHER(0, "其他"),
    INSERT(1, "新增"),
    UPDATE(2, "修改"),
    DELETE(3, "删除"),
    SELECT(4, "查询"),
    EXPORT(5, "导出"),
    IMPORT(6, "导入"),
    LOGIN(7, "登录"),
    LOGOUT(8, "登出"),
    FORCE_LOGOUT(9, "强退"),
    CLEAN(10, "清空数据"),
    APPROVE(11, "审批"),
    REJECT(12, "驳回"),
    CANCEL(13, "取消"),
    ENABLE(14, "启用"),
    DISABLE(15, "禁用"),
    ASSIGN(16, "分配"),
    RESET_PWD(17, "重置密码"),
    UPLOAD(18, "上传"),
    DOWNLOAD(19, "下载");

    @EnumValue
    @JsonValue
    private final Integer code;

    private final String name;

    BusinessType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
