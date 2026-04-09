package com.xiaohong.lease.web.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaohong.lease.model.entity.ScheduledTaskLog;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 定时任务执行日志Service
 *
 * @author 小红
 */
public interface ScheduledTaskLogService extends IService<ScheduledTaskLog> {

    /**
     * 记录任务执行日志
     */
    void logTaskExecution(ScheduledTaskLog log);

    /**
     * 查询任务执行统计
     */
    List<Map<String, Object>> getTaskStatistics(Date startTime, Date endTime);

    /**
     * 查询最近执行的任务日志
     */
    List<ScheduledTaskLog> getRecentLogs(Integer limit);

    /**
     * 清理指定日期之前的日志
     */
    int cleanupLogsBefore(Date date);

    /**
     * 获取任务成功率
     */
    Double getTaskSuccessRate(Long taskId, Date startTime, Date endTime);
}
