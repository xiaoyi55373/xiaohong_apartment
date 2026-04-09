package com.xiaohong.lease.web.app.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.xiaohong.lease.web.app.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {
    private static final Logger log = LoggerFactory.getLogger(SmsServiceImpl.class);

    @Autowired(required = false)
    private Client client;

    @Value("${sms.test-mode:false}")
    private Boolean testMode;

    @Override
    public void sendCode(String phone, String code) {
        // 测试模式：验证码输出到控制台，不发送真实短信
        if (Boolean.TRUE.equals(testMode)) {
            log.info("========================================");
            log.info("【测试模式】短信验证码");
            log.info("手机号: {}", phone);
            log.info("验证码: {}", code);
            log.info("========================================");
            return;
        }

        // 生产模式：调用阿里云短信服务
        SendSmsRequest smsRequest = new SendSmsRequest();
        smsRequest.setSignName("小红公寓");
        smsRequest.setTemplateCode("SMS_154950909");
        String param = """
                {"code":"%s"}
                """.formatted(code);
        smsRequest.setTemplateParam(param);
        smsRequest.setPhoneNumbers(phone);

        try {
            if (client == null) {
                throw new RuntimeException("阿里云短信客户端未配置");
            }
            client.sendSms(smsRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
