package com.xiaohong.lease.common.operationlog.event;

import com.xiaohong.lease.model.entity.SystemOperationLog;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 操作日志事件
 *
 * @author 小红
 */
@Getter
public class OperationLogEvent extends ApplicationEvent {

    private final SystemOperationLog operationLog;

    public OperationLogEvent(Object source, SystemOperationLog operationLog) {
        super(source);
        this.operationLog = operationLog;
    }
}
