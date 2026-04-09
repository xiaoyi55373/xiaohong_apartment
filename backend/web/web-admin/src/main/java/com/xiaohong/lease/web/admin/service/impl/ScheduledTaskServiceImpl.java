package com.xiaohong.lease.web.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaohong.lease.model.entity.ScheduledTask;
import com.xiaohong.lease.model.enums.BaseStatus;
import com.xiaohong.lease.model.enums.TaskType;
import com.xiaohong.lease.model.vo.schedule.TaskExecuteResult;
import com.xiaohong.lease.web.admin.mapper.ScheduledTaskMapper;
import com.xiaohong.lease.web.admin.schedule.ScheduleTaskExecutor;
import com.xiaohong.lease.web.admin.service.ScheduledTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 定时任务配置Service实现
 *
 * @author 小红
 */
@Service
public class ScheduledTaskServiceImpl extends ServiceImpl<ScheduledTaskMapper, ScheduledTask> 
        implements ScheduledTaskService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTaskServiceImpl.class);

    @Autowired
    private ScheduledTaskMapper scheduledTaskMapper;

    @Autowired
    private ScheduleTaskExecutor taskExecutor;

    @Override
    public List<ScheduledTask> listEnabledTasks() {
        return scheduledTaskMapper.selectEnabledTasks();
    }

    @Override
    public ScheduledTask getByTaskType(String taskType) {
        return scheduledTaskMapper.selectByTaskType(taskType);
    }

    @Override
    public void updateExecuteStatus(Long taskId, TaskExecuteResult result) {
        String status = result.getSuccess() ? "SUCCESS" : "FAIL";
        scheduledTaskMapper.updateExecuteStatus(taskId, new Date(), status, result.getMessage());
    }

    @Override
    public void enableTask(Long taskId) {
        ScheduledTask task = getById(taskId);
        if (task != null) {
            task.setStatus(BaseStatus.ENABLE);
            updateById(task);
            log.info("启用定时任务: {}", task.getTaskName());
        }
    }

    @Override
    public void disableTask(Long taskId) {
        ScheduledTask task = getById(taskId);
        if (task != null) {
            task.setStatus(BaseStatus.DISABLE);
            updateById(task);
            log.info("禁用定时任务: {}", task.getTaskName());
        }
    }

    @Override
    public TaskExecuteResult executeTaskManually(Long taskId) {
        ScheduledTask task = getById(taskId);
        if (task == null) {
            return TaskExecuteResult.fail("任务不存在");
        }
        return taskExecutor.executeTask(task);
    }

    @Override
    public void initDefaultTasks() {
        // 初始化租约到期提醒任务（提前7天）
        initTaskIfNotExists(
            TaskType.LEASE_EXPIRE_REMIND,
            "租约到期提醒（7天）",
            "检查7天内到期的租约并发送提醒通知",
            "0 0 9 * * ?", // 每天上午9点执行
            7,
            "BOTH"
        );

        // 初始化预约过期处理任务
        initTaskIfNotExists(
            TaskType.APPOINTMENT_EXPIRE,
            "预约过期处理",
            "自动处理已过期的预约记录",
            "0 0 1 * * ?", // 每天凌晨1点执行
            null,
            null
        );

        // 初始化数据清理任务
        initTaskIfNotExists(
            TaskType.DATA_CLEANUP,
            "数据清理",
            "清理过期的操作日志和登录日志（保留90天）",
            "0 0 2 * * 0", // 每周日凌晨2点执行
            null,
            null
        );

        // 初始化浏览历史清理任务
        initTaskIfNotExists(
            TaskType.BROWSING_HISTORY_CLEANUP,
            "浏览历史清理",
            "清理过期的浏览历史记录（保留30天）",
            "0 0 3 * * 0", // 每周日凌晨3点执行
            null,
            null
        );

        log.info("定时任务初始化完成");
    }

    private void initTaskIfNotExists(TaskType taskType, String name, String description, 
                                     String cronExpression, Integer remindDays, String notifyType) {
        ScheduledTask existingTask = getByTaskType(taskType.getStringCode());
        if (existingTask == null) {
            ScheduledTask task = new ScheduledTask();
            task.setTaskName(name);
            task.setTaskDescription(description);
            task.setTaskType(taskType.getStringCode());
            task.setCronExpression(cronExpression);
            task.setStatus(BaseStatus.ENABLE);
            task.setNotifyEnabled(1);
            task.setNotifyType(notifyType);
            task.setRemindDaysBefore(remindDays);
            task.setExecuteCount(0);
            task.setSuccessCount(0);
            task.setFailCount(0);
            save(task);
            log.info("创建默认定时任务: {}", name);
        }
    }
}
