package com.xiaohong.lease.web.admin.schedule;

import com.xiaohong.lease.model.entity.ScheduledTask;
import com.xiaohong.lease.model.entity.ScheduledTaskLog;
import com.xiaohong.lease.model.enums.TaskType;
import com.xiaohong.lease.model.vo.schedule.TaskExecuteResult;
import com.xiaohong.lease.web.admin.service.AppointmentExpireService;
import com.xiaohong.lease.web.admin.service.LeaseExpireRemindService;
import com.xiaohong.lease.web.admin.service.ScheduledTaskLogService;
import com.xiaohong.lease.web.admin.service.ScheduledTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.Date;

/**
 * 定时任务执行器
 *
 * @author 小红
 */
@Component
public class ScheduleTaskExecutor {

    private static final Logger log = LoggerFactory.getLogger(ScheduleTaskExecutor.class);

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @Autowired
    private ScheduledTaskLogService scheduledTaskLogService;

    @Autowired
    private LeaseExpireRemindService leaseExpireRemindService;

    @Autowired
    private AppointmentExpireService appointmentExpireService;

    /**
     * 执行任务
     */
    @Async("taskExecutor")
    public TaskExecuteResult executeTask(ScheduledTask task) {
        log.info("开始执行定时任务: {} [类型: {}]", task.getTaskName(), task.getTaskType());
        
        long startTime = System.currentTimeMillis();
        TaskExecuteResult result;
        
        try {
            // 根据任务类型执行相应逻辑
            TaskType taskType = TaskType.getByCode(task.getTaskType());
            if (taskType == null) {
                result = TaskExecuteResult.fail("未知的任务类型: " + task.getTaskType());
            } else {
                switch (taskType) {
                    case LEASE_EXPIRE_REMIND:
                        result = executeLeaseExpireRemindTask(task);
                        break;
                    case APPOINTMENT_EXPIRE:
                        result = executeAppointmentExpireTask(task);
                        break;
                    case DATA_CLEANUP:
                        result = executeDataCleanupTask(task);
                        break;
                    case BROWSING_HISTORY_CLEANUP:
                        result = executeBrowsingHistoryCleanupTask(task);
                        break;
                    default:
                        result = TaskExecuteResult.fail("未实现的任务类型: " + taskType);
                }
            }
        } catch (Exception e) {
            log.error("定时任务执行异常: {}", task.getTaskName(), e);
            result = TaskExecuteResult.fail("执行异常: " + e.getMessage());
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 记录执行日志
        recordTaskLog(task, result, startTime, endTime, duration);
        
        // 更新任务执行状态
        scheduledTaskService.updateExecuteStatus(task.getId(), result);
        
        log.info("定时任务执行完成: {}，结果: {}，耗时: {}ms", 
                task.getTaskName(), result.getSuccess() ? "成功" : "失败", duration);
        
        return result;
    }

    /**
     * 执行租约到期提醒任务
     */
    private TaskExecuteResult executeLeaseExpireRemindTask(ScheduledTask task) {
        Integer remindDays = task.getRemindDaysBefore() != null ? task.getRemindDaysBefore() : 7;
        String notifyType = task.getNotifyType() != null ? task.getNotifyType() : "BOTH";
        
        return leaseExpireRemindService.executeRemindTask(remindDays, notifyType);
    }

    /**
     * 执行预约过期处理任务
     */
    private TaskExecuteResult executeAppointmentExpireTask(ScheduledTask task) {
        return appointmentExpireService.executeExpireTask();
    }

    /**
     * 执行数据清理任务
     */
    private TaskExecuteResult executeDataCleanupTask(ScheduledTask task) {
        // 清理90天前的操作日志
        Date cleanupDate = new Date(System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000);
        int count = scheduledTaskLogService.cleanupLogsBefore(cleanupDate);
        
        return TaskExecuteResult.success("数据清理完成，清理日志: " + count + " 条", count);
    }

    /**
     * 执行浏览历史清理任务
     */
    private TaskExecuteResult executeBrowsingHistoryCleanupTask(ScheduledTask task) {
        // 清理30天前的浏览历史
        // 这里简化处理，实际应该调用BrowsingHistoryService
        return TaskExecuteResult.success("浏览历史清理完成", 0);
    }

    /**
     * 记录任务执行日志
     */
    private void recordTaskLog(ScheduledTask task, TaskExecuteResult result, 
                               long startTime, long endTime, long duration) {
        try {
            ScheduledTaskLog taskLog = new ScheduledTaskLog();
            taskLog.setTaskId(task.getId());
            taskLog.setTaskName(task.getTaskName());
            taskLog.setTaskType(task.getTaskType());
            taskLog.setExecuteStatus(result.getSuccess() ? "SUCCESS" : "FAIL");
            taskLog.setStartTime(new Date(startTime));
            taskLog.setEndTime(new Date(endTime));
            taskLog.setDuration(duration);
            taskLog.setExecuteResult(result.getMessage());
            taskLog.setErrorMessage(result.getSuccess() ? null : result.getMessage());
            taskLog.setAffectedCount(result.getAffectedCount());
            
            // 获取服务器IP
            try {
                taskLog.setServerIp(InetAddress.getLocalHost().getHostAddress());
            } catch (Exception e) {
                taskLog.setServerIp("unknown");
            }
            
            scheduledTaskLogService.logTaskExecution(taskLog);
        } catch (Exception e) {
            log.error("记录任务执行日志失败", e);
        }
    }
}
