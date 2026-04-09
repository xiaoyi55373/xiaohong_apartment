package com.xiaohong.lease.web.app.service.impl;

import com.xiaohong.lease.common.exception.LeaseException;
import com.xiaohong.lease.common.result.ResultCodeEnum;
import com.xiaohong.lease.common.utils.JWTUtil;
import com.xiaohong.lease.model.entity.UserInfo;
import com.xiaohong.lease.model.enums.BaseStatus;
import com.xiaohong.lease.common.wx.WxMiniProgramClient;
import com.xiaohong.lease.web.app.mapper.UserInfoMapper;
import com.xiaohong.lease.web.app.service.MiniProgramLoginService;
import com.xiaohong.lease.web.app.vo.user.MiniProgramLoginVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MiniProgramLoginServiceImpl implements MiniProgramLoginService {

    @Autowired
    private WxMiniProgramClient wxMiniProgramClient;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public String login(MiniProgramLoginVo loginVo) {
        if (!StringUtils.hasText(loginVo.getCode())) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR);
        }

        WxMiniProgramClient.Code2SessionResponse response = wxMiniProgramClient.code2Session(loginVo.getCode());
        if (!response.isSuccess()) {
            throw new LeaseException(ResultCodeEnum.SERVICE_ERROR);
        }

        String openid = response.getOpenid();
        String sessionKey = response.getSessionKey();
        String unionid = response.getUnionid();

        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getOpenid, openid);
        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);

        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setOpenid(openid);
            userInfo.setUnionid(unionid);
            userInfo.setMiniProgramSessionKey(sessionKey);
            userInfo.setStatus(BaseStatus.ENABLE);
            userInfo.setNickname("微信用户" + openid.substring(openid.length() - 6));
            userInfoMapper.insert(userInfo);
        } else {
            if (userInfo.getStatus() == BaseStatus.DISABLE) {
                throw new LeaseException(ResultCodeEnum.APP_ACCOUNT_DISABLED_ERROR);
            }
            // 更新 session_key 和 unionid
            userInfo.setMiniProgramSessionKey(sessionKey);
            if (StringUtils.hasText(unionid)) {
                userInfo.setUnionid(unionid);
            }
            userInfoMapper.updateById(userInfo);
        }

        return JWTUtil.createToken(userInfo.getId(), userInfo.getNickname());
    }
}
