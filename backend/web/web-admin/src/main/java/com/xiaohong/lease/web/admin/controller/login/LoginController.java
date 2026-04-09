package com.xiaohong.lease.web.admin.controller.login;


import com.xiaohong.lease.common.context.LoginUserContext;
import com.xiaohong.lease.common.operationlog.annotation.OperationLog;
import com.xiaohong.lease.common.ratelimit.RateLimitType;
import com.xiaohong.lease.common.ratelimit.annotation.RateLimit;
import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.model.enums.BusinessType;
import com.xiaohong.lease.model.enums.OperatorType;
import com.xiaohong.lease.web.admin.service.LoginService;
import com.xiaohong.lease.web.admin.vo.login.CaptchaVo;
import com.xiaohong.lease.web.admin.vo.login.LoginVo;
import com.xiaohong.lease.web.admin.vo.system.user.SystemUserInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台管理系统登录管理")
@RestController
@RequestMapping("/admin")
public class LoginController {

    @Autowired
    private LoginService service;

    @Operation(summary = "获取图形验证码")
    @GetMapping("login/captcha")
    @RateLimit(type = RateLimitType.IP, window = 60, maxRequests = 10)
    public Result<CaptchaVo> getCaptcha() {
        CaptchaVo captchaVo = service.getCaptcha();
        return Result.ok(captchaVo);
    }

    @Operation(summary = "退出登录")
    @PostMapping("system/index/logout")
    @OperationLog(title = "用户登录", businessType = BusinessType.LOGOUT, operatorType = OperatorType.SYSTEM_USER)
    public Result logout() {
        // 清除登录状态逻辑
        return Result.ok();
    }

    @Operation(summary = "登录")
    @PostMapping("login")
    @RateLimit(type = RateLimitType.IP, window = 60, maxRequests = 5, message = "登录尝试过于频繁，请稍后再试")
    @OperationLog(title = "用户登录", businessType = BusinessType.LOGIN, operatorType = OperatorType.SYSTEM_USER, 
                  saveParam = false, desc = "用户登录操作")
    public Result<String> login(@RequestBody LoginVo loginVo) {
        String token = service.login(loginVo);
        return Result.ok(token);
    }

    @Operation(summary = "获取登陆用户个人信息")
    @GetMapping("info")
    public Result<SystemUserInfoVo> info() {
        SystemUserInfoVo user = service.getSystemUserInfoById(LoginUserContext.getLoginUser().getUserId());
        return Result.ok(user);
    }
}
