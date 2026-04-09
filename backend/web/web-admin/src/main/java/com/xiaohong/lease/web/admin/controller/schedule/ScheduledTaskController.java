package com.xiaohong.lease.web.admin.controller.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.model.entity.ScheduledTask;
import com.xiaohong.lease.model.entity.ScheduledTaskLog;
import com.xiaohong.lease.model.vo.schedule.TaskExecuteResult;
import com.xiaohong.lease.web.admin.service.ScheduledTaskLogService;
import com.xiaohong.lease.web.admin.service.ScheduledTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 定时任务管理控制器
 *
 * @author 小红
 */
@Tag(name = "定时任务管理")
@RestController
@RequestMapping("/admin/schedule")
public class ScheduledTaskController {

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @Autowired
    private ScheduledTaskLogService scheduledTaskLogService;

    @Operation(summary = "获取定时任务列表")
    @GetMapping("/task/list")
    public Result<List<ScheduledTask>> listTasks() {
        List<ScheduledTask> list = scheduledTaskService.list();
        return Result.ok(list);
    }

    @Operation(summary = "分页查询定时任务")
    @GetMapping("/task/page")
    public Result<IPage<ScheduledTask>> pageTasks(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        IPage<ScheduledTask> pageParam = new Page<>(page, size);
        IPage<ScheduledTask> result = scheduledTaskService.page(pageParam);
        return Result.ok(result);
    }

    @Operation(summary = "获取定时任务详情")
    @GetMapping("/task/{id}")
    public Result<ScheduledTask> getTask(@Parameter(description = "任务ID") @PathVariable Long id) {
        ScheduledTask task = scheduledTaskService.getById(id);
        return Result.ok(task);
    }

    @Operation(summary = "创建定时任务")
    @PostMapping("/task")
    public Result<Void> createTask(@RequestBody ScheduledTask task) {
        task.setExecuteCount(0);
        task.setSuccessCount(0);
        task.setFailCount(0);
        scheduledTaskService.save(task);
        return Result.ok();
    }

    @Operation(summary = "更新定时任务")
    @PutMapping("/task/{id}")
    public Result<Void> updateTask(@Parameter(description = "任务ID") @PathVariable Long id, 
                                   @RequestBody ScheduledTask task) {
        task.setId(id);
        scheduledTaskService.updateById(task);
        return Result.ok();
    }

    @Operation(summary = "删除定时任务")
    @DeleteMapping("/task/{id}")
    public Result<Void> deleteTask(@Parameter(description = "任务ID") @PathVariable Long id) {
        scheduledTaskService.removeById(id);
        return Result.ok();
    }

    @Operation(summary = "启用定时任务")
    @PutMapping("/task/{id}/enable")
    public Result<Void> enableTask(@Parameter(description = "任务ID") @PathVariable Long id) {
        scheduledTaskService.enableTask(id);
        return Result.ok();
    }

    @Operation(summary = "禁用定时任务")
    @PutMapping("/task/{id}/disable")
    public Result<Void> disableTask(@Parameter(description = "任务ID") @PathVariable Long id) {
        scheduledTaskService.disableTask(id);
        return Result.ok();
    }

    @Operation(summary = "手动执行定时任务")
    @PostMapping("/task/{id}/execute")
    public Result<TaskExecuteResult> executeTask(@Parameter(description = "任务ID") @PathVariable Long id) {
        TaskExecuteResult result = scheduledTaskService.executeTaskManually(id);
        if (result.getSuccess()) {
            return Result.ok(result);
        } else {
            return Result.fail(result.getMessage());
        }
    }

    @Operation(summary = "获取任务执行日志")
    @GetMapping("/task/{id}/logs")
    public Result<IPage<ScheduledTaskLog>> getTaskLogs(
            @Parameter(description = "任务ID") @PathVariable Long id,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        IPage<ScheduledTaskLog> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ScheduledTaskLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduledTaskLog::getTaskId, id);
        wrapper.orderByDesc(ScheduledTaskLog::getStartTime);
        IPage<ScheduledTaskLog> result = scheduledTaskLogService.page(pageParam, wrapper);
        return Result.ok(result);
    }

    @Operation(summary = "获取最近执行日志")
    @GetMapping("/logs/recent")
    public Result<List<ScheduledTaskLog>> getRecentLogs(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "20") Integer limit) {
        List<ScheduledTaskLog> logs = scheduledTaskLogService.getRecentLogs(limit);
        return Result.ok(logs);
    }

    @Operation(summary = "获取任务执行统计")
    @GetMapping("/statistics")
    public Result<List<Map<String, Object>>> getTaskStatistics(
            @Parameter(description = "开始时间") @RequestParam(required = false) Date startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) Date endTime) {
        if (startTime == null) {
            startTime = new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000); // 默认7天前
        }
        if (endTime == null) {
            endTime = new Date();
        }
        List<Map<String, Object>> statistics = scheduledTaskLogService.getTaskStatistics(startTime, endTime);
        return Result.ok(statistics);
    }

    @Operation(summary = "清理历史日志")
    @DeleteMapping("/logs/cleanup")
    public Result<Integer> cleanupLogs(
            @Parameter(description = "保留天数") @RequestParam(defaultValue = "90") Integer keepDays) {
        Date cleanupDate = new Date(System.currentTimeMillis() - keepDays * 24 * 60 * 60 * 1000L);
        int count = scheduledTaskLogService.cleanupLogsBefore(cleanupDate);
        return Result.ok(count);
    }
}
