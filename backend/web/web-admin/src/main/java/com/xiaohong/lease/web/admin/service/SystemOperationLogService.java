package com.xiaohong.lease.web.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaohong.lease.model.entity.SystemOperationLog;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SystemOperationLogService extends IService<SystemOperationLog> {

    /**
     * 分页查询操作日志
     */
    IPage<SystemOperationLog> pageItem(IPage<SystemOperationLog> page, String title, String operName, 
                                        String createTimeBegin, String createTimeEnd);

    /**
     * 根据业务类型统计
     */
    List<Map<String, Object>> getStatisticsByBusinessType(Date startTime, Date endTime);

    /**
     * 根据状态统计
     */
    Map<String, Long> getStatisticsByStatus(Date startTime, Date endTime);

    /**
     * 获取操作日志趋势
     */
    List<Map<String, Object>> getTrendByDate(Date startTime, Date endTime);

    /**
     * 清空操作日志（保留最近N天）
     */
    boolean cleanLog(int keepDays);

    /**
     * 异步保存操作日志
     */
    void asyncSaveLog(SystemOperationLog log);
}
