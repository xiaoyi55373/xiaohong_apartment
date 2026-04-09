package com.xiaohong.lease.web.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaohong.lease.model.entity.SystemOperationLog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SystemOperationLogMapper extends BaseMapper<SystemOperationLog> {

    /**
     * 根据业务类型统计操作日志
     */
    List<Map<String, Object>> selectStatisticsByBusinessType(
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime);

    /**
     * 根据状态统计操作日志
     */
    List<Map<String, Object>> selectStatisticsByStatus(
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime);

    /**
     * 按日期统计操作日志趋势
     */
    List<Map<String, Object>> selectTrendByDate(
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime);
}
