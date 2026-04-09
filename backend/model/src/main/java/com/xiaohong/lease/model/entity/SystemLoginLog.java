package com.xiaohong.lease.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Schema(description = "后台登录日志")
@TableName("system_login_log")
@Data
@EqualsAndHashCode(callSuper = false)
public class SystemLoginLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名")
    @TableField("username")
    private String username;

    @Schema(description = "登录IP地址")
    @TableField("ip_addr")
    private String ipAddr;

    @Schema(description = "登录地点")
    @TableField("login_location")
    private String loginLocation;

    @Schema(description = "浏览器类型")
    @TableField("browser")
    private String browser;

    @Schema(description = "操作系统")
    @TableField("os")
    private String os;

    @Schema(description = "登录状态")
    @TableField("status")
    private Integer status;

    @Schema(description = "提示消息")
    @TableField("msg")
    private String msg;

    @Schema(description = "登录时间")
    @TableField("login_time")
    private Date loginTime;
}
