package com.xiaohong.lease.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaohong.lease.model.enums.BehaviorType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 用户行为日志表
 *
 * @author 小红
 */
@Schema(description = "用户行为日志")
@TableName("user_behavior_log")
@Data
@EqualsAndHashCode(callSuper = false)
public class UserBehaviorLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "行为类型")
    @TableField("behavior_type")
    private BehaviorType behaviorType;

    @Schema(description = "行为描述")
    @TableField("behavior_desc")
    private String behaviorDesc;

    @Schema(description = "目标类型")
    @TableField("target_type")
    private String targetType;

    @Schema(description = "目标ID")
    @TableField("target_id")
    private Long targetId;

    @Schema(description = "请求URL")
    @TableField("request_url")
    private String requestUrl;

    @Schema(description = "请求参数")
    @TableField("request_param")
    private String requestParam;

    @Schema(description = "用户IP")
    @TableField("user_ip")
    private String userIp;

    @Schema(description = "用户代理")
    @TableField("user_agent")
    private String userAgent;

    @Schema(description = "设备类型")
    @TableField("device_type")
    private String deviceType;

    @Schema(description = "来源页面")
    @TableField("source_page")
    private String sourcePage;

    @Schema(description = "行为时间")
    @TableField("behavior_time")
    private Date behaviorTime;

    @Schema(description = "额外数据(JSON)")
    @TableField("extra_data")
    private String extraData;
}
