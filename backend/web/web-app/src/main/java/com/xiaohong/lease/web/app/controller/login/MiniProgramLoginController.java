package com.xiaohong.lease.web.app.controller.login;

import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.web.app.service.MiniProgramLoginService;
import com.xiaohong.lease.web.app.vo.user.MiniProgramLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "小程序登录管理")
@RequestMapping("/mini/")
public class MiniProgramLoginController {

    @Autowired
    private MiniProgramLoginService miniProgramLoginService;

    @PostMapping("login")
    @Operation(summary = "微信小程序登录")
    public Result<String> login(@RequestBody MiniProgramLoginVo loginVo) {
        String token = miniProgramLoginService.login(loginVo);
        return Result.ok(token);
    }
}
