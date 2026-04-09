package com.xiaohong.lease.common.sms;

/**
 * 短信服务接口
 * 
 * 通用短信服务接口，供所有模块使用
 *
 * @author 小红
 */
public interface SmsService {

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @param code  验证码
     */
    void sendCode(String phone, String code);

    /**
     * 发送普通短信
     *
     * @param phone   手机号
     * @param content 短信内容
     */
    default void sendMessage(String phone, String content) {
        // 默认实现，子类可覆盖
        throw new UnsupportedOperationException("该方法尚未实现");
    }
}
