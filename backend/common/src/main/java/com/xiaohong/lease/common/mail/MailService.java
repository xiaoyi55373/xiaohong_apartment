package com.xiaohong.lease.common.mail;

import java.util.Map;

/**
 * 邮件服务接口
 *
 * @author 小红
 */
public interface MailService {

    /**
     * 发送简单文本邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    void sendSimpleMail(String to, String subject, String content);

    /**
     * 发送HTML格式邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param html    HTML内容
     */
    void sendHtmlMail(String to, String subject, String html);

    /**
     * 使用模板发送邮件
     *
     * @param to           收件人邮箱
     * @param subject      邮件主题
     * @param templateName 模板名称
     * @param variables    模板变量
     */
    void sendTemplateMail(String to, String subject, String templateName, Map<String, Object> variables);

    /**
     * 发送验证码邮件
     *
     * @param to       收件人邮箱
     * @param code     验证码
     * @param expireMinutes 过期时间(分钟)
     */
    void sendVerificationCodeMail(String to, String code, Integer expireMinutes);

    /**
     * 发送预约确认邮件
     *
     * @param to            收件人邮箱
     * @param userName      用户名
     * @param apartmentName 公寓名称
     * @param appointmentTime 预约时间
     * @param address       公寓地址
     */
    void sendAppointmentConfirmationMail(String to, String userName, String apartmentName, 
                                          String appointmentTime, String address);

    /**
     * 发送租约到期提醒邮件
     *
     * @param to            收件人邮箱
     * @param userName      用户名
     * @param apartmentName 公寓名称
     * @param expireDate    到期日期
     * @param daysRemaining 剩余天数
     */
    void sendLeaseExpirationReminderMail(String to, String userName, String apartmentName,
                                          String expireDate, Integer daysRemaining);

    /**
     * 发送密码重置邮件
     *
     * @param to          收件人邮箱
     * @param userName    用户名
     * @param resetToken  重置令牌
     * @param expireHours 过期时间(小时)
     */
    void sendPasswordResetMail(String to, String userName, String resetToken, Integer expireHours);
}
