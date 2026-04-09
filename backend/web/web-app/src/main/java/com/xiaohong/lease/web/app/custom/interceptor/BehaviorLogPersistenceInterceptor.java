package com.xiaohong.lease.web.app.custom.interceptor;

import com.xiaohong.lease.common.log.aspect.BehaviorLogAspect;
import com.xiaohong.lease.model.entity.UserBehaviorLog;
import com.xiaohong.lease.web.app.service.UserBehaviorLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 行为日志持久化拦截器
 * 在请求完成后将ThreadLocal中的行为日志异步保存到数据库
 *
 * @author 小红
 */
@Component
public class BehaviorLogPersistenceInterceptor implements HandlerInterceptor {

    @Autowired
    private UserBehaviorLogService userBehaviorLogService;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserBehaviorLog behaviorLog = BehaviorLogAspect.BehaviorLogHolder.getCurrentLog();
        if (behaviorLog != null) {
            userBehaviorLogService.asyncSave(behaviorLog);
            BehaviorLogAspect.BehaviorLogHolder.clear();
        }
    }
}
