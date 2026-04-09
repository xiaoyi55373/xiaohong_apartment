package com.xiaohong.lease.model.vo.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 任务执行结果
 *
 * @author 小红
 */
@Data
@Builder
@Schema(description = "定时任务执行结果")
public class TaskExecuteResult {

    @Schema(description = "是否执行成功")
    private Boolean success;

    @Schema(description = "执行结果描述")
    private String message;

    @Schema(description = "受影响记录数")
    private Integer affectedCount;

    @Schema(description = "执行详情")
    private String details;

    /**
     * 创建成功结果
     */
    public static TaskExecuteResult success(String message, Integer affectedCount) {
        return TaskExecuteResult.builder()
                .success(true)
                .message(message)
                .affectedCount(affectedCount)
                .build();
    }

    /**
     * 创建成功结果
     */
    public static TaskExecuteResult success(String message, Integer affectedCount, String details) {
        return TaskExecuteResult.builder()
                .success(true)
                .message(message)
                .affectedCount(affectedCount)
                .details(details)
                .build();
    }

    /**
     * 创建失败结果
     */
    public static TaskExecuteResult fail(String message) {
        return TaskExecuteResult.builder()
                .success(false)
                .message(message)
                .affectedCount(0)
                .build();
    }

    /**
     * 创建失败结果
     */
    public static TaskExecuteResult fail(String message, String details) {
        return TaskExecuteResult.builder()
                .success(false)
                .message(message)
                .affectedCount(0)
                .details(details)
                .build();
    }
}
