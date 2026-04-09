package com.xiaohong.lease.web.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaohong.lease.model.entity.ScheduledTask;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

/**
 * 定时任务配置Mapper
 *
 * @author 小红
 */
public interface ScheduledTaskMapper extends BaseMapper<ScheduledTask> {

    /**
     * 查询所有启用的任务
     */
    List<ScheduledTask> selectEnabledTasks();

    /**
     * 根据任务类型查询任务
     */
    ScheduledTask selectByTaskType(@Param("taskType") String taskType);

    /**
     * 更新任务执行状态
     */
    @Update("UPDATE scheduled_task SET last_execute_time = #{executeTime}, " +
            "last_execute_status = #{status}, last_execute_result = #{result}, " +
            "execute_count = execute_count + 1, " +
            "success_count = CASE WHEN #{status} = 'SUCCESS' THEN success_count + 1 ELSE success_count END, " +
            "fail_count = CASE WHEN #{status} = 'FAIL' THEN fail_count + 1 ELSE fail_count END " +
            "WHERE id = #{taskId}")
    int updateExecuteStatus(@Param("taskId") Long taskId,
                           @Param("executeTime") Date executeTime,
                           @Param("status") String status,
                           @Param("result") String result);
}
