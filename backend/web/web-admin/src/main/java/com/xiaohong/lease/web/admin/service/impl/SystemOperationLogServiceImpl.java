package com.xiaohong.lease.web.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaohong.lease.model.entity.SystemOperationLog;
import com.xiaohong.lease.web.admin.mapper.SystemOperationLogMapper;
import com.xiaohong.lease.web.admin.service.SystemOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台操作日志Service实现
 *
 * @author 小红
 */
@Service
@Slf4j
public class SystemOperationLogServiceImpl extends ServiceImpl<SystemOperationLogMapper, SystemOperationLog> implements SystemOperationLogService {

    @Override
    public IPage<SystemOperationLog> pageItem(IPage<SystemOperationLog> page, String title, String operName,
                                               String createTimeBegin, String createTimeEnd) {
        LambdaQueryWrapper<SystemOperationLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(title), SystemOperationLog::getTitle, title)
                .like(StringUtils.isNotBlank(operName), SystemOperationLog::getOperName, operName)
                .ge(StringUtils.isNotBlank(createTimeBegin), SystemOperationLog::getOperTime, createTimeBegin)
                .le(StringUtils.isNotBlank(createTimeEnd), SystemOperationLog::getOperTime, createTimeEnd)
                .orderByDesc(SystemOperationLog::getOperTime);
        return page(page, queryWrapper);
    }

    @Override
    public List<Map<String, Object>> getStatisticsByBusinessType(Date startTime, Date endTime) {
        return baseMapper.selectStatisticsByBusinessType(startTime, endTime);
    }

    @Override
    public Map<String, Long> getStatisticsByStatus(Date startTime, Date endTime) {
        List<Map<String, Object>> list = baseMapper.selectStatisticsByStatus(startTime, endTime);
        Map<String, Long> result = new HashMap<>();
        for (Map<String, Object> map : list) {
            Integer status = (Integer) map.get("status");
            Long count = ((Number) map.get("count")).longValue();
            result.put(status != null && status == 1 ? "success" : "fail", count);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getTrendByDate(Date startTime, Date endTime) {
        return baseMapper.selectTrendByDate(startTime, endTime);
    }

    @Override
    public boolean cleanLog(int keepDays) {
        try {
            LambdaQueryWrapper<SystemOperationLog> queryWrapper = new LambdaQueryWrapper<>();
            Date keepDate = new Date(System.currentTimeMillis() - keepDays * 24L * 60 * 60 * 1000);
            queryWrapper.lt(SystemOperationLog::getOperTime, keepDate);
            return remove(queryWrapper);
        } catch (Exception e) {
            log.error("清理操作日志失败", e);
            return false;
        }
    }

    @Override
    @Async("operationLogExecutor")
    public void asyncSaveLog(SystemOperationLog operationLog) {
        try {
            this.save(operationLog);
        } catch (Exception e) {
            log.error("异步保存操作日志失败", e);
        }
    }
}
