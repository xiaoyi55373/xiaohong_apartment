package com.xiaohong.lease.web.app.service.impl;

import com.xiaohong.lease.common.constant.RedisConstant;
import com.xiaohong.lease.common.exception.LeaseException;
import com.xiaohong.lease.common.result.ResultCodeEnum;
import com.xiaohong.lease.common.utils.JWTUtil;
import com.xiaohong.lease.common.utils.SmsUtil;
import com.xiaohong.lease.model.entity.UserInfo;
import com.xiaohong.lease.model.enums.BaseStatus;
import com.xiaohong.lease.web.app.mapper.UserInfoMapper;
import com.xiaohong.lease.web.app.service.LoginService;
import com.xiaohong.lease.web.app.service.SmsService;
import com.xiaohong.lease.web.app.vo.user.LoginVo;
import com.xiaohong.lease.web.app.vo.user.UserInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private SmsService smsService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public void getCode(String phone) {
        String code = SmsUtil.generateCode(6);
        String key = RedisConstant.APP_LOGIN_PREFIX + phone;


        Boolean hasKey = stringRedisTemplate.hasKey(key);
        if (hasKey) {
            Long ttl = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (RedisConstant.APP_LOGIN_CODE_TTL_SEC - ttl < RedisConstant.APP_LOGIN_CODE_RESEND_TIME_SEC) {
                throw new LeaseException(ResultCodeEnum.APP_SEND_SMS_TOO_OFTEN);
            }
        }

        smsService.sendCode(phone, code);
        stringRedisTemplate.opsForValue().set(key, code, RedisConstant.APP_LOGIN_CODE_TTL_SEC, TimeUnit.SECONDS);
    }

    @Override
    public String login(LoginVo loginVo) {

        if (!StringUtils.hasText(loginVo.getPhone())) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_PHONE_EMPTY);
        }

        // 测试模式：跳过验证码验证，任何验证码都能通过
        System.out.println("========================================");
        System.out.println("【测试模式】跳过验证码验证");
        System.out.println("手机号: " + loginVo.getPhone());
        System.out.println("输入的验证码: " + loginVo.getCode());
        System.out.println("========================================");

        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getPhone, loginVo.getPhone());
        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setPhone(loginVo.getPhone());
            userInfo.setStatus(BaseStatus.ENABLE);
            userInfo.setNickname("小红-" + loginVo.getPhone().substring(7));
            userInfoMapper.insert(userInfo);
        } else {
            if (userInfo.getStatus() == BaseStatus.DISABLE) {
                throw new LeaseException(ResultCodeEnum.APP_ACCOUNT_DISABLED_ERROR);
            }
        }

        return JWTUtil.createToken(userInfo.getId(), userInfo.getPhone());
    }

    @Override
    public UserInfoVo getUserInfoById(Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        if (userInfo == null) {
            return new UserInfoVo("未知用户", "");
        }
        return new UserInfoVo(userInfo.getNickname(), userInfo.getAvatarUrl());
    }
}
