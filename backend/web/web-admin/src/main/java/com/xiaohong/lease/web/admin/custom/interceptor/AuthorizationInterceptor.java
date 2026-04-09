package com.xiaohong.lease.web.admin.custom.interceptor;

import com.xiaohong.lease.common.annotation.CheckPermission;
import com.xiaohong.lease.common.context.LoginUserContext;
import com.xiaohong.lease.common.exception.LeaseException;
import com.xiaohong.lease.common.result.ResultCodeEnum;
import com.xiaohong.lease.model.entity.SystemUser;
import com.xiaohong.lease.model.enums.SystemUserType;
import com.xiaohong.lease.web.admin.service.SystemUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {


    @Autowired
    SystemUserService systemUserService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {


        //2.判断当前用户是否为管理员用户
        SystemUser systemUser = systemUserService.getById(LoginUserContext.getLoginUser().getUserId());
        if (systemUser.getType().equals(SystemUserType.ADMIN)) {
            //2.1若为管理员，则不需鉴权
            return true;
        } else {
            CheckPermission permission = null;
            if (handler instanceof HandlerMethod handlerMethod) {
                permission = handlerMethod.getMethodAnnotation(CheckPermission.class);
            }
            if (permission == null) {
                return true;
            } else {
                throw new LeaseException(ResultCodeEnum.ADMIN_ACCESS_FORBIDDEN);
            }
        }
    }

}
