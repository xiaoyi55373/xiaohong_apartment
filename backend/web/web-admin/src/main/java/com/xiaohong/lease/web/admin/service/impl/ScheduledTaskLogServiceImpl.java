package com.xiaohong.lease.web.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaohong.lease.model.entity.ScheduledTaskLog;
import com.xiaohong.lease.web.admin.mapper.ScheduledTaskLogMapper;
import com.xiaohong.lease.web.admin.service.ScheduledTaskLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 定时任务执行日志Service实现
 *
 * @author 小红
 */
@Service
public class ScheduledTaskLogServiceImpl extends ServiceImpl<ScheduledTaskLogMapper, ScheduledTaskLog> 
        implements ScheduledTaskLogService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTaskLogServiceImpl.class);

    @Autowired
    private ScheduledTaskLogMapper scheduledTaskLogMapper;

    @Override
    public void logTaskExecution(ScheduledTaskLog taskLog) {
        save(taskLog);
    }

    @Override
    public List<Map<String, Object>> getTaskStatistics(Date startTime, Date endTime) {
        return scheduledTaskLogMapper.selectTaskStatistics(startTime, endTime);
    }

    @Override
    public List<ScheduledTaskLog> getRecentLogs(Integer limit) {
        return scheduledTaskLogMapper.selectRecentLogs(limit);
    }

    @Override
    public int cleanupLogsBefore(Date date) {
        int count = scheduledTaskLogMapper.deleteLogsBefore(date);
        log.info("清理定时任务日志: {} 条，日期: {}", count, date);
        return count;
    }

    @Override
    public Double getTaskSuccessRate(Long taskId, Date startTime, Date endTime) {
        LambdaQueryWrapper<ScheduledTaskLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(taskId != null, ScheduledTaskLog::getTaskId, taskId)
               .ge(startTime != null, ScheduledTaskLog::getStartTime, startTime)
               .le(endTime != null, ScheduledTaskLog::getStartTime, endTime);
        
        long total = count(wrapper);
        if (total == 0) {
            return 100.0;
        }
        
        wrapper.eq(ScheduledTaskLog::getExecuteStatus, "SUCCESS");
        long success = count(wrapper);
        
        return (double) success / total * 100;
    }
}
