package com.xiaohong.lease.web.app.service;

import com.xiaohong.lease.web.app.vo.user.MiniProgramLoginVo;

public interface MiniProgramLoginService {

    /**
     * 微信小程序登录
     *
     * @param loginVo 登录请求参数
     * @return JWT token
     */
    String login(MiniProgramLoginVo loginVo);
}
