package com.xiaohong.lease.web.app.controller.login;


import com.xiaohong.lease.common.annotation.CheckPermission;
import com.xiaohong.lease.common.context.LoginUserContext;
import com.xiaohong.lease.common.exception.LeaseException;
import com.xiaohong.lease.common.log.annotation.BehaviorLog;
import com.xiaohong.lease.common.ratelimit.RateLimitType;
import com.xiaohong.lease.common.ratelimit.annotation.RateLimit;
import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.common.result.ResultCodeEnum;
import com.xiaohong.lease.model.enums.BehaviorType;
import com.xiaohong.lease.web.app.service.LoginService;
import com.xiaohong.lease.web.app.vo.user.LoginVo;
import com.xiaohong.lease.web.app.vo.user.UserInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "登录管理")
@RequestMapping("/app/")
public class LoginController {

    @Autowired
    private LoginService service;

    @GetMapping("login/getCode")
    @Operation(summary = "获取短信验证码")
    @BehaviorLog(value = BehaviorType.CLICK, desc = "获取短信验证码", targetType = "login", saveParam = false)
    @RateLimit(type = RateLimitType.IP, window = 60, maxRequests = 3, message = "短信发送过于频繁，请稍后再试")
    public Result getCode(@RequestParam String phone) {

        if ("13888888888".equals(phone)) {
            throw new LeaseException(ResultCodeEnum.ADMIN_ACCESS_FORBIDDEN);
        }

        service.getCode(phone);
        return Result.ok();
    }

    @PostMapping("login")
    @Operation(summary = "登录")
    @BehaviorLog(value = BehaviorType.LOGIN, desc = "用户登录", targetType = "login", saveParam = false)
    @RateLimit(type = RateLimitType.IP, window = 60, maxRequests = 5, message = "登录尝试过于频繁，请稍后再试")
    public Result<String> login(@RequestBody LoginVo loginVo) {
        String token = service.login(loginVo);
        return Result.ok(token);
    }

    @GetMapping("info")
    @Operation(summary = "获取登录用户信息")
    @BehaviorLog(value = BehaviorType.BROWSE, desc = "获取用户信息", targetType = "user")
    public Result<UserInfoVo> info() {
        UserInfoVo userInfoVo = service.getUserInfoById(LoginUserContext.getLoginUser().getUserId());
        return Result.ok(userInfoVo);
    }
}
