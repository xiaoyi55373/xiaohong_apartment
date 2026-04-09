package com.xiaohong.lease.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaohong.lease.model.enums.BaseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 定时任务配置实体
 *
 * @author 小红
 */
@Schema(description = "定时任务配置表")
@TableName(value = "scheduled_task")
@Data
public class ScheduledTask extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "任务名称")
    @TableField(value = "task_name")
    private String taskName;

    @Schema(description = "任务描述")
    @TableField(value = "task_description")
    private String taskDescription;

    @Schema(description = "任务类型：LEASE_EXPIRE_REMIND-租约到期提醒, APPOINTMENT_EXPIRE-预约过期处理, DATA_CLEANUP-数据清理")
    @TableField(value = "task_type")
    private String taskType;

    @Schema(description = "Cron表达式")
    @TableField(value = "cron_expression")
    private String cronExpression;

    @Schema(description = "任务状态：ENABLE-启用, DISABLE-禁用")
    @TableField(value = "status")
    private BaseStatus status;

    @Schema(description = "是否发送通知：0-否, 1-是")
    @TableField(value = "notify_enabled")
    private Integer notifyEnabled;

    @Schema(description = "通知方式：SMS-短信, EMAIL-邮件, BOTH-两者")
    @TableField(value = "notify_type")
    private String notifyType;

    @Schema(description = "提前提醒天数（用于租约到期提醒）")
    @TableField(value = "remind_days_before")
    private Integer remindDaysBefore;

    @Schema(description = "任务参数（JSON格式）")
    @TableField(value = "task_params")
    private String taskParams;

    @Schema(description = "最后执行时间")
    @TableField(value = "last_execute_time")
    private java.util.Date lastExecuteTime;

    @Schema(description = "最后执行状态：SUCCESS-成功, FAIL-失败")
    @TableField(value = "last_execute_status")
    private String lastExecuteStatus;

    @Schema(description = "最后执行结果")
    @TableField(value = "last_execute_result")
    private String lastExecuteResult;

    @Schema(description = "执行次数")
    @TableField(value = "execute_count")
    private Integer executeCount;

    @Schema(description = "成功次数")
    @TableField(value = "success_count")
    private Integer successCount;

    @Schema(description = "失败次数")
    @TableField(value = "fail_count")
    private Integer failCount;
}
