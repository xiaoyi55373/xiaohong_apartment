package com.xiaohong.lease.web.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaohong.lease.model.entity.ScheduledTask;
import com.xiaohong.lease.model.vo.schedule.TaskExecuteResult;

import java.util.List;

/**
 * 定时任务配置Service
 *
 * @author 小红
 */
public interface ScheduledTaskService extends IService<ScheduledTask> {

    /**
     * 查询所有启用的任务
     */
    List<ScheduledTask> listEnabledTasks();

    /**
     * 根据任务类型查询任务
     */
    ScheduledTask getByTaskType(String taskType);

    /**
     * 更新任务执行状态
     */
    void updateExecuteStatus(Long taskId, TaskExecuteResult result);

    /**
     * 启用任务
     */
    void enableTask(Long taskId);

    /**
     * 禁用任务
     */
    void disableTask(Long taskId);

    /**
     * 手动执行任务
     */
    TaskExecuteResult executeTaskManually(Long taskId);

    /**
     * 初始化默认任务
     */
    void initDefaultTasks();
}
