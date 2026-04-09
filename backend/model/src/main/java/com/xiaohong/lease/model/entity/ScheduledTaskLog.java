package com.xiaohong.lease.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 定时任务执行日志实体
 *
 * @author 小红
 */
@Schema(description = "定时任务执行日志表")
@TableName(value = "scheduled_task_log")
@Data
public class ScheduledTaskLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "任务ID")
    @TableField(value = "task_id")
    private Long taskId;

    @Schema(description = "任务名称")
    @TableField(value = "task_name")
    private String taskName;

    @Schema(description = "任务类型")
    @TableField(value = "task_type")
    private String taskType;

    @Schema(description = "执行状态：SUCCESS-成功, FAIL-失败")
    @TableField(value = "execute_status")
    private String executeStatus;

    @Schema(description = "执行开始时间")
    @TableField(value = "start_time")
    private Date startTime;

    @Schema(description = "执行结束时间")
    @TableField(value = "end_time")
    private Date endTime;

    @Schema(description = "执行耗时（毫秒）")
    @TableField(value = "duration")
    private Long duration;

    @Schema(description = "执行结果")
    @TableField(value = "execute_result")
    private String executeResult;

    @Schema(description = "错误信息")
    @TableField(value = "error_message")
    private String errorMessage;

    @Schema(description = "受影响记录数")
    @TableField(value = "affected_count")
    private Integer affectedCount;

    @Schema(description = "服务器IP")
    @TableField(value = "server_ip")
    private String serverIp;
}
