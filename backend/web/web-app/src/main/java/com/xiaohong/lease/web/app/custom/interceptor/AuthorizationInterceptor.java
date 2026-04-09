package com.xiaohong.lease.web.app.custom.interceptor;

import com.xiaohong.lease.common.annotation.CheckPermission;
import com.xiaohong.lease.common.context.LoginUserContext;
import com.xiaohong.lease.common.exception.LeaseException;
import com.xiaohong.lease.common.result.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 如果用户未登录，跳过权限检查
        if (LoginUserContext.getLoginUser() == null) {
            return true;
        }
        
        String phone = LoginUserContext.getLoginUser().getUsername();
        if ("13888888888".equals(phone)) {
            CheckPermission permission = null;
            if (handler instanceof HandlerMethod handlerMethod) {
                permission = handlerMethod.getMethodAnnotation(CheckPermission.class);
            }
            if (permission == null) {
                return true;
            } else {
                throw new LeaseException(ResultCodeEnum.ADMIN_ACCESS_FORBIDDEN);
            }
        } else {
            return true;
        }
    }

}
