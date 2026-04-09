package com.xiaohong.lease.web.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaohong.lease.model.entity.ScheduledTaskLog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 定时任务执行日志Mapper
 *
 * @author 小红
 */
public interface ScheduledTaskLogMapper extends BaseMapper<ScheduledTaskLog> {

    /**
     * 查询任务执行统计
     */
    @Select("SELECT task_type, COUNT(*) as count, " +
            "SUM(CASE WHEN execute_status = 'SUCCESS' THEN 1 ELSE 0 END) as success_count, " +
            "SUM(CASE WHEN execute_status = 'FAIL' THEN 1 ELSE 0 END) as fail_count " +
            "FROM scheduled_task_log " +
            "WHERE start_time >= #{startTime} AND start_time <= #{endTime} " +
            "GROUP BY task_type")
    List<Map<String, Object>> selectTaskStatistics(@Param("startTime") Date startTime, 
                                                   @Param("endTime") Date endTime);

    /**
     * 查询最近执行的任务日志
     */
    List<ScheduledTaskLog> selectRecentLogs(@Param("limit") Integer limit);

    /**
     * 清理指定日期之前的日志
     */
    int deleteLogsBefore(@Param("date") Date date);
}
