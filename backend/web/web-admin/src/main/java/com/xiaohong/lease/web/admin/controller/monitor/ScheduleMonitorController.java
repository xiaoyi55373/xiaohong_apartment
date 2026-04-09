package com.xiaohong.lease.web.admin.controller.monitor;

import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.common.schedule.ScheduleConfiguration;
import com.xiaohong.lease.common.schedule.ScheduleMetrics;
import com.xiaohong.lease.common.schedule.ScheduleTaskManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时任务监控控制器
 *
 * @author 小红
 */
@Slf4j
@Tag(name = "定时任务监控管理")
@RestController
@RequestMapping("/admin/schedule")
public class ScheduleMonitorController {

    @Autowired
    private ScheduleTaskManager taskManager;

    @Autowired
    private ScheduleMetrics scheduleMetrics;

    @Autowired
    private ScheduleConfiguration scheduleConfiguration;

    /**
     * 获取定时任务列表
     */
    @Operation(summary = "获取定时任务列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> listTasks() {
        List<Map<String, Object>> tasks = new ArrayList<>();

        // 租约状态检查任务
        tasks.add(createTaskInfo(
                "leaseStatusCheck",
                "租约状态检查",
                "每天凌晨0点执行，将到期租约状态更新为已过期",
                "0 0 0 * * ?",
                true
        ));

        // 租约到期提醒任务
        tasks.add(createTaskInfo(
                "leaseReminder",
                "租约到期提醒",
                "每天早上9点执行，发送到期提醒通知",
                "0 0 9 * * ?",
                scheduleConfiguration.getLeaseReminder().isEnabled()
        ));

        // 预约过期处理任务
        tasks.add(createTaskInfo(
                "appointmentExpire",
                "预约过期处理",
                "每天晚上23点执行，将过期预约标记为已过期",
                "0 0 23 * * ?",
                scheduleConfiguration.getAppointmentExpire().isEnabled()
        ));

        // 每日数据统计任务
        tasks.add(createTaskInfo(
                "dailyStatistics",
                "每日数据统计",
                "每天凌晨1点执行，生成昨日数据统计",
                "0 0 1 * * ?",
                scheduleConfiguration.getStatistics().isEnabled()
        ));

        // 数据清理任务
        tasks.add(createTaskInfo(
                "dataCleanup",
                "数据清理",
                "每天凌晨2点执行，清理过期数据",
                "0 0 2 * * ?",
                scheduleConfiguration.getDataCleanup().isEnabled()
        ));

        return Result.ok(tasks);
    }

    /**
     * 创建任务信息
     */
    private Map<String, Object> createTaskInfo(String taskName, String taskDesc, 
                                                String taskDetail, String cron, boolean enabled) {
        Map<String, Object> task = new HashMap<>();
        task.put("name", taskName);
        task.put("description", taskDesc);
        task.put("detail", taskDetail);
        task.put("cron", cron);
        task.put("enabled", enabled);
        
        // 获取最后执行时间
        LocalDateTime lastExecutionTime = taskManager.getLastExecutionTime(taskName);
        task.put("lastExecutionTime", lastExecutionTime);
        
        // 获取执行指标
        ScheduleMetrics.TaskMetrics metrics = scheduleMetrics.getMetrics(taskName);
        if (metrics != null) {
            task.put("successCount", metrics.getSuccessCount().get());
            task.put("failCount", metrics.getFailCount().get());
            task.put("successRate", String.format("%.2f%%", metrics.getSuccessRate()));
            task.put("avgDurationMs", String.format("%.2f", metrics.getAvgDurationMs()));
            task.put("lastDurationMs", metrics.getLastDurationMs());
        } else {
            task.put("successCount", 0);
            task.put("failCount", 0);
            task.put("successRate", "0.00%");
            task.put("avgDurationMs", "0.00");
            task.put("lastDurationMs", 0);
        }
        
        return task;
    }

    /**
     * 获取定时任务配置
     */
    @Operation(summary = "获取定时任务配置")
    @GetMapping("/config")
    public Result<Map<String, Object>> getConfig() {
        Map<String, Object> config = new HashMap<>();
        
        // 全局配置
        config.put("enabled", scheduleConfiguration.isEnabled());
        
        // 租约提醒配置
        Map<String, Object> leaseReminder = new HashMap<>();
        leaseReminder.put("enabled", scheduleConfiguration.getLeaseReminder().isEnabled());
        leaseReminder.put("reminderDays", scheduleConfiguration.getLeaseReminder().getReminderDays());
        leaseReminder.put("reminderTime", scheduleConfiguration.getLeaseReminder().getReminderTime());
        leaseReminder.put("sendSms", scheduleConfiguration.getLeaseReminder().isSendSms());
        leaseReminder.put("sendEmail", scheduleConfiguration.getLeaseReminder().isSendEmail());
        config.put("leaseReminder", leaseReminder);
        
        // 预约过期配置
        Map<String, Object> appointmentExpire = new HashMap<>();
        appointmentExpire.put("enabled", scheduleConfiguration.getAppointmentExpire().isEnabled());
        appointmentExpire.put("expireHours", scheduleConfiguration.getAppointmentExpire().getExpireHours());
        appointmentExpire.put("processTime", scheduleConfiguration.getAppointmentExpire().getProcessTime());
        config.put("appointmentExpire", appointmentExpire);
        
        // 数据统计配置
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("enabled", scheduleConfiguration.getStatistics().isEnabled());
        statistics.put("statisticsTime", scheduleConfiguration.getStatistics().getStatisticsTime());
        statistics.put("retentionDays", scheduleConfiguration.getStatistics().getRetentionDays());
        config.put("statistics", statistics);
        
        // 数据清理配置
        Map<String, Object> dataCleanup = new HashMap<>();
        dataCleanup.put("enabled", scheduleConfiguration.getDataCleanup().isEnabled());
        dataCleanup.put("cleanupTime", scheduleConfiguration.getDataCleanup().getCleanupTime());
        dataCleanup.put("operationLogRetentionDays", scheduleConfiguration.getDataCleanup().getOperationLogRetentionDays());
        dataCleanup.put("loginLogRetentionDays", scheduleConfiguration.getDataCleanup().getLoginLogRetentionDays());
        dataCleanup.put("behaviorLogRetentionDays", scheduleConfiguration.getDataCleanup().getBehaviorLogRetentionDays());
        config.put("dataCleanup", dataCleanup);
        
        return Result.ok(config);
    }

    /**
     * 获取任务执行指标
     */
    @Operation(summary = "获取任务执行指标")
    @GetMapping("/metrics/{taskName}")
    public Result<Map<String, Object>> getTaskMetrics(@PathVariable String taskName) {
        ScheduleMetrics.TaskMetrics metrics = scheduleMetrics.getMetrics(taskName);
        
        if (metrics == null) {
            return Result.ok(null);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("taskName", taskName);
        result.put("successCount", metrics.getSuccessCount().get());
        result.put("failCount", metrics.getFailCount().get());
        result.put("totalCount", metrics.getTotalCount());
        result.put("successRate", String.format("%.2f%%", metrics.getSuccessRate()));
        result.put("runningCount", metrics.getRunningCount().get());
        result.put("lastStartTime", metrics.getLastStartTime());
        result.put("lastEndTime", metrics.getLastEndTime());
        result.put("lastDurationMs", metrics.getLastDurationMs());
        result.put("avgDurationMs", String.format("%.2f", metrics.getAvgDurationMs()));
        
        return Result.ok(result);
    }

    /**
     * 获取所有任务执行指标
     */
    @Operation(summary = "获取所有任务执行指标")
    @GetMapping("/metrics")
    public Result<Map<String, Map<String, Object>>> getAllMetrics() {
        Map<String, ScheduleMetrics.TaskMetrics> allMetrics = scheduleMetrics.getAllMetrics();
        Map<String, Map<String, Object>> result = new HashMap<>();
        
        for (Map.Entry<String, ScheduleMetrics.TaskMetrics> entry : allMetrics.entrySet()) {
            ScheduleMetrics.TaskMetrics metrics = entry.getValue();
            Map<String, Object> metricMap = new HashMap<>();
            metricMap.put("successCount", metrics.getSuccessCount().get());
            metricMap.put("failCount", metrics.getFailCount().get());
            metricMap.put("totalCount", metrics.getTotalCount());
            metricMap.put("successRate", String.format("%.2f%%", metrics.getSuccessRate()));
            metricMap.put("runningCount", metrics.getRunningCount().get());
            metricMap.put("lastStartTime", metrics.getLastStartTime());
            metricMap.put("lastEndTime", metrics.getLastEndTime());
            metricMap.put("lastDurationMs", metrics.getLastDurationMs());
            metricMap.put("avgDurationMs", String.format("%.2f", metrics.getAvgDurationMs()));
            
            result.put(entry.getKey(), metricMap);
        }
        
        return Result.ok(result);
    }

    /**
     * 重置任务指标
     */
    @Operation(summary = "重置任务指标")
    @PostMapping("/metrics/{taskName}/reset")
    public Result<Void> resetMetrics(@PathVariable String taskName) {
        scheduleMetrics.resetMetrics(taskName);
        log.info("重置任务指标: {}", taskName);
        return Result.ok();
    }

    /**
     * 重置所有任务指标
     */
    @Operation(summary = "重置所有任务指标")
    @PostMapping("/metrics/reset")
    public Result<Void> resetAllMetrics() {
        scheduleMetrics.resetAllMetrics();
        log.info("重置所有任务指标");
        return Result.ok();
    }

    /**
     * 手动触发任务执行（仅用于测试）
     */
    @Operation(summary = "手动触发任务执行")
    @PostMapping("/trigger/{taskName}")
    public Result<String> triggerTask(@PathVariable String taskName) {
        log.info("手动触发任务: {}", taskName);
        
        // 这里可以添加手动触发逻辑
        // 实际项目中可能需要通过消息队列或其他方式触发
        
        return Result.ok("任务触发请求已发送，任务名称: " + taskName);
    }
}
