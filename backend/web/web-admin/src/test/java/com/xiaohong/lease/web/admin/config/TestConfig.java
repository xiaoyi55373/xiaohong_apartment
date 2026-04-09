package com.xiaohong.lease.web.admin.config;

import com.xiaohong.lease.common.utils.JWTUtil;
import com.xiaohong.lease.web.admin.service.LoginService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 测试配置类
 * 用于测试环境特殊配置
 */
@Configuration
@Profile("test")
public class TestConfig {

    /**
     * 创建一个模拟的 LoginService，用于测试时跳过验证码验证
     */
    @Bean
    @Primary
    public LoginService loginService() {
        LoginService mockLoginService = Mockito.mock(LoginService.class);
        
        // 模拟 getCaptcha 方法
        when(mockLoginService.getCaptcha()).thenAnswer(invocation -> {
            return new com.xiaohong.lease.web.admin.vo.login.CaptchaVo(
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==",
                "TEST_CAPTCHA_KEY_12345"
            );
        });
        
        // 模拟 login 方法，任何验证码都能通过
        when(mockLoginService.login(any())).thenAnswer(invocation -> {
            com.xiaohong.lease.web.admin.vo.login.LoginVo loginVo = invocation.getArgument(0);
            // 使用 JWTUtil 创建有效的 token
            return JWTUtil.createToken(1L, loginVo.getUsername());
        });
        
        return mockLoginService;
    }
}
