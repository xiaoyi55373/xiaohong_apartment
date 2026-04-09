package com.xiaohong.lease.web.app.controller.mini.user;

import com.xiaohong.lease.common.annotation.CheckPermission;
import com.xiaohong.lease.common.context.LoginUser;
import com.xiaohong.lease.common.context.LoginUserContext;
import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.model.entity.UserInfo;
import com.xiaohong.lease.web.app.custom.wx.WxPhoneDecryptUtil;
import com.xiaohong.lease.web.app.mapper.UserInfoMapper;
import com.xiaohong.lease.web.app.vo.user.DecryptPhoneVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序端用户控制器
 *
 * @author 小红
 * @createDate 2026-04-07
 */
@RestController
@Tag(name = "小程序-用户", description = "小程序端用户相关接口")
@RequestMapping("/mini/user")
public class MiniUserController {

    @Autowired
    private UserInfoMapper userInfoMapper;

    /**
     * 获取当前登录用户信息
     */
    @Operation(summary = "获取当前登录用户信息", description = "获取当前登录用户的详细信息")
    @CheckPermission
    @GetMapping("/info")
    public Result<UserInfo> info() {
        LoginUser loginUser = LoginUserContext.getLoginUser();
        UserInfo userInfo = userInfoMapper.selectById(loginUser.getUserId());
        return Result.ok(userInfo);
    }

    /**
     * 解密微信手机号
     */
    @Operation(summary = "解密微信手机号", description = "解密微信手机号并更新用户手机号")
    @CheckPermission
    @PostMapping("/phone/decrypt")
    public Result<String> decryptPhone(@RequestBody DecryptPhoneVo decryptPhoneVo) {
        LoginUser loginUser = LoginUserContext.getLoginUser();
        UserInfo userInfo = userInfoMapper.selectById(loginUser.getUserId());
        String phone = WxPhoneDecryptUtil.decryptPhoneNumber(
                userInfo.getMiniProgramSessionKey(),
                decryptPhoneVo.getEncryptedData(),
                decryptPhoneVo.getIv()
        );
        userInfo.setPhone(phone);
        userInfoMapper.updateById(userInfo);
        return Result.ok(phone);
    }
}
