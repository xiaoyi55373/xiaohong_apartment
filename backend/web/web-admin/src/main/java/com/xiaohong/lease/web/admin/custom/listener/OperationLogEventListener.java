package com.xiaohong.lease.web.admin.custom.listener;

import com.xiaohong.lease.common.operationlog.event.OperationLogEvent;
import com.xiaohong.lease.model.entity.SystemOperationLog;
import com.xiaohong.lease.web.admin.service.SystemOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 操作日志事件监听器
 *
 * @author 小红
 */
@Component
@Slf4j
public class OperationLogEventListener {

    @Autowired
    private SystemOperationLogService systemOperationLogService;

    @EventListener
    @Async("operationLogExecutor")
    public void handleOperationLogEvent(OperationLogEvent event) {
        SystemOperationLog operationLog = event.getOperationLog();
        if (operationLog == null) {
            return;
        }
        try {
            systemOperationLogService.save(operationLog);
        } catch (Exception e) {
            log.error("异步保存操作日志失败", e);
        }
    }
}
