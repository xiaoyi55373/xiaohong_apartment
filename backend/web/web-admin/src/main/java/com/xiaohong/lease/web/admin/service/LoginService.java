package com.xiaohong.lease.web.admin.service;

import com.xiaohong.lease.web.admin.vo.login.CaptchaVo;
import com.xiaohong.lease.web.admin.vo.login.LoginVo;
import com.xiaohong.lease.web.admin.vo.system.user.SystemUserInfoVo;

public interface LoginService {

    CaptchaVo getCaptcha();

    String login(LoginVo loginVo);

    SystemUserInfoVo getSystemUserInfoById(Long userId);
}
