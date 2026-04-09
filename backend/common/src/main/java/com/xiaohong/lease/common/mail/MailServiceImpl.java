package com.xiaohong.lease.common.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

/**
 * 邮件服务实现类
 *
 * @author 小红
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "spring.mail", name = "host")
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    @Autowired(required = false)
    private TemplateEngine templateEngine;

    /**
     * 获取发件人地址
     */
    private String getFromAddress() {
        return mailProperties.getFromName() + " <" + mailProperties.getUsername() + ">";
    }

    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(getFromAddress());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            log.info("简单邮件已发送至: {}", to);
        } catch (Exception e) {
            log.error("发送简单邮件失败: {}", e.getMessage(), e);
            throw new RuntimeException("邮件发送失败", e);
        }
    }

    @Override
    public void sendHtmlMail(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, mailProperties.getEncoding());
            helper.setFrom(mailProperties.getUsername(), mailProperties.getFromName());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("HTML邮件已发送至: {}", to);
        } catch (Exception e) {
            log.error("发送HTML邮件失败: {}", e.getMessage(), e);
            throw new RuntimeException("邮件发送失败", e);
        }
    }

    @Override
    public void sendTemplateMail(String to, String subject, String templateName, Map<String, Object> variables) {
        if (templateEngine == null) {
            log.error("模板引擎未配置，无法发送模板邮件");
            throw new RuntimeException("模板引擎未配置");
        }

        try {
            Context context = new Context();
            context.setVariables(variables);
            String html = templateEngine.process("mail/" + templateName, context);
            sendHtmlMail(to, subject, html);
            log.info("模板邮件已发送至: {}, 模板: {}", to, templateName);
        } catch (Exception e) {
            log.error("发送模板邮件失败: {}", e.getMessage(), e);
            throw new RuntimeException("模板邮件发送失败", e);
        }
    }

    @Override
    public void sendVerificationCodeMail(String to, String code, Integer expireMinutes) {
        String subject = "【小红公寓】验证码";
        
        // 如果模板引擎可用，使用模板
        if (templateEngine != null) {
            Map<String, Object> variables = Map.of(
                "code", code,
                "expireMinutes", expireMinutes,
                "brandName", "小红公寓",
                "brandSlogan", "让租房更简单"
            );
            sendTemplateMail(to, subject, "verification-code", variables);
        } else {
            // 使用简单HTML
            String html = buildVerificationCodeHtml(code, expireMinutes);
            sendHtmlMail(to, subject, html);
        }
    }

    @Override
    public void sendAppointmentConfirmationMail(String to, String userName, String apartmentName,
                                                 String appointmentTime, String address) {
        String subject = "【小红公寓】预约看房确认";
        
        if (templateEngine != null) {
            Map<String, Object> variables = Map.of(
                "userName", userName,
                "apartmentName", apartmentName,
                "appointmentTime", appointmentTime,
                "address", address,
                "brandName", "小红公寓"
            );
            sendTemplateMail(to, subject, "appointment-confirmation", variables);
        } else {
            String html = buildAppointmentConfirmationHtml(userName, apartmentName, appointmentTime, address);
            sendHtmlMail(to, subject, html);
        }
    }

    @Override
    public void sendLeaseExpirationReminderMail(String to, String userName, String apartmentName,
                                                 String expireDate, Integer daysRemaining) {
        String subject = "【小红公寓】租约到期提醒";
        
        if (templateEngine != null) {
            Map<String, Object> variables = Map.of(
                "userName", userName,
                "apartmentName", apartmentName,
                "expireDate", expireDate,
                "daysRemaining", daysRemaining,
                "brandName", "小红公寓"
            );
            sendTemplateMail(to, subject, "lease-expiration-reminder", variables);
        } else {
            String html = buildLeaseExpirationHtml(userName, apartmentName, expireDate, daysRemaining);
            sendHtmlMail(to, subject, html);
        }
    }

    @Override
    public void sendPasswordResetMail(String to, String userName, String resetToken, Integer expireHours) {
        String subject = "【小红公寓】密码重置";
        String resetUrl = "https://xiaohong.com/reset-password?token=" + resetToken;
        
        if (templateEngine != null) {
            Map<String, Object> variables = Map.of(
                "userName", userName,
                "resetUrl", resetUrl,
                "expireHours", expireHours,
                "brandName", "小红公寓"
            );
            sendTemplateMail(to, subject, "password-reset", variables);
        } else {
            String html = buildPasswordResetHtml(userName, resetUrl, expireHours);
            sendHtmlMail(to, subject, html);
        }
    }

    /**
     * 构建验证码邮件HTML
     */
    private String buildVerificationCodeHtml(String code, Integer expireMinutes) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>验证码</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background-color: #f5f5f5;">
                <table role="presentation" style="width: 100%%; border-collapse: collapse;">
                    <tr>
                        <td align="center" style="padding: 40px 0;">
                            <table role="presentation" style="width: 600px; border-collapse: collapse; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.08);">
                                <!-- Header -->
                                <tr>
                                    <td style="padding: 40px 40px 20px; text-align: center; background: linear-gradient(135deg, #FF6B6B 0%%, #FF8E8E 100%%); border-radius: 12px 12px 0 0;">
                                        <h1 style="margin: 0; color: #ffffff; font-size: 24px; font-weight: 600;">小红公寓</h1>
                                        <p style="margin: 8px 0 0; color: rgba(255,255,255,0.9); font-size: 14px;">让租房更简单</p>
                                    </td>
                                </tr>
                                <!-- Content -->
                                <tr>
                                    <td style="padding: 40px;">
                                        <h2 style="margin: 0 0 20px; color: #333333; font-size: 20px; font-weight: 600;">验证码</h2>
                                        <p style="margin: 0 0 24px; color: #666666; font-size: 14px; line-height: 1.6;">您好，您的验证码如下，请在<strong>%d分钟</strong>内使用：</p>
                                        <div style="text-align: center; padding: 30px; background-color: #FFF5F5; border-radius: 8px; margin: 24px 0;">
                                            <span style="font-size: 36px; font-weight: 700; color: #FF6B6B; letter-spacing: 8px;">%s</span>
                                        </div>
                                        <p style="margin: 24px 0 0; color: #999999; font-size: 12px; line-height: 1.6;">如非本人操作，请忽略此邮件。</p>
                                    </td>
                                </tr>
                                <!-- Footer -->
                                <tr>
                                    <td style="padding: 20px 40px; text-align: center; border-top: 1px solid #EEEEEE;">
                                        <p style="margin: 0; color: #999999; font-size: 12px;">小红公寓 © 2026 版权所有</p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(expireMinutes, code);
    }

    /**
     * 构建预约确认邮件HTML
     */
    private String buildAppointmentConfirmationHtml(String userName, String apartmentName,
                                                     String appointmentTime, String address) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>预约确认</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background-color: #f5f5f5;">
                <table role="presentation" style="width: 100%%; border-collapse: collapse;">
                    <tr>
                        <td align="center" style="padding: 40px 0;">
                            <table role="presentation" style="width: 600px; border-collapse: collapse; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.08);">
                                <tr>
                                    <td style="padding: 40px 40px 20px; text-align: center; background: linear-gradient(135deg, #FF6B6B 0%%, #FF8E8E 100%%); border-radius: 12px 12px 0 0;">
                                        <h1 style="margin: 0; color: #ffffff; font-size: 24px; font-weight: 600;">小红公寓</h1>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 40px;">
                                        <h2 style="margin: 0 0 20px; color: #333333; font-size: 20px; font-weight: 600;">预约看房确认</h2>
                                        <p style="margin: 0 0 16px; color: #666666; font-size: 14px;">尊敬的 %s，您好！</p>
                                        <p style="margin: 0 0 24px; color: #666666; font-size: 14px; line-height: 1.6;">您的看房预约已确认，详情如下：</p>
                                        <div style="background-color: #FFF5F5; border-radius: 8px; padding: 24px; margin: 24px 0;">
                                            <p style="margin: 0 0 12px; color: #333333; font-size: 14px;"><strong>公寓名称：</strong>%s</p>
                                            <p style="margin: 0 0 12px; color: #333333; font-size: 14px;"><strong>预约时间：</strong>%s</p>
                                            <p style="margin: 0; color: #333333; font-size: 14px;"><strong>公寓地址：</strong>%s</p>
                                        </div>
                                        <p style="margin: 24px 0 0; color: #999999; font-size: 12px;">如需取消或修改预约，请提前联系客服。</p>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 20px 40px; text-align: center; border-top: 1px solid #EEEEEE;">
                                        <p style="margin: 0; color: #999999; font-size: 12px;">小红公寓 © 2026 版权所有</p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(userName, apartmentName, appointmentTime, address);
    }

    /**
     * 构建租约到期提醒邮件HTML
     */
    private String buildLeaseExpirationHtml(String userName, String apartmentName,
                                             String expireDate, Integer daysRemaining) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>租约到期提醒</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background-color: #f5f5f5;">
                <table role="presentation" style="width: 100%%; border-collapse: collapse;">
                    <tr>
                        <td align="center" style="padding: 40px 0;">
                            <table role="presentation" style="width: 600px; border-collapse: collapse; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.08);">
                                <tr>
                                    <td style="padding: 40px 40px 20px; text-align: center; background: linear-gradient(135deg, #FF6B6B 0%%, #FF8E8E 100%%); border-radius: 12px 12px 0 0;">
                                        <h1 style="margin: 0; color: #ffffff; font-size: 24px; font-weight: 600;">小红公寓</h1>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 40px;">
                                        <h2 style="margin: 0 0 20px; color: #333333; font-size: 20px; font-weight: 600;">租约到期提醒</h2>
                                        <p style="margin: 0 0 16px; color: #666666; font-size: 14px;">尊敬的 %s，您好！</p>
                                        <p style="margin: 0 0 24px; color: #666666; font-size: 14px; line-height: 1.6;">您的租约即将到期，请及时处理续租或退租事宜。</p>
                                        <div style="background-color: #FFF5F5; border-radius: 8px; padding: 24px; margin: 24px 0;">
                                            <p style="margin: 0 0 12px; color: #333333; font-size: 14px;"><strong>公寓名称：</strong>%s</p>
                                            <p style="margin: 0 0 12px; color: #333333; font-size: 14px;"><strong>到期日期：</strong>%s</p>
                                            <p style="margin: 0; color: #FF6B6B; font-size: 14px; font-weight: 600;"><strong>剩余天数：%d天</strong></p>
                                        </div>
                                        <p style="margin: 24px 0 0; color: #999999; font-size: 12px;">如需续租，请提前联系房东或客服。</p>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 20px 40px; text-align: center; border-top: 1px solid #EEEEEE;">
                                        <p style="margin: 0; color: #999999; font-size: 12px;">小红公寓 © 2026 版权所有</p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(userName, apartmentName, expireDate, daysRemaining);
    }

    /**
     * 构建密码重置邮件HTML
     */
    private String buildPasswordResetHtml(String userName, String resetUrl, Integer expireHours) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>密码重置</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background-color: #f5f5f5;">
                <table role="presentation" style="width: 100%%; border-collapse: collapse;">
                    <tr>
                        <td align="center" style="padding: 40px 0;">
                            <table role="presentation" style="width: 600px; border-collapse: collapse; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.08);">
                                <tr>
                                    <td style="padding: 40px 40px 20px; text-align: center; background: linear-gradient(135deg, #FF6B6B 0%%, #FF8E8E 100%%); border-radius: 12px 12px 0 0;">
                                        <h1 style="margin: 0; color: #ffffff; font-size: 24px; font-weight: 600;">小红公寓</h1>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 40px;">
                                        <h2 style="margin: 0 0 20px; color: #333333; font-size: 20px; font-weight: 600;">密码重置</h2>
                                        <p style="margin: 0 0 16px; color: #666666; font-size: 14px;">尊敬的 %s，您好！</p>
                                        <p style="margin: 0 0 24px; color: #666666; font-size: 14px; line-height: 1.6;">您申请了密码重置，请点击下方按钮完成重置（%d小时内有效）：</p>
                                        <div style="text-align: center; margin: 32px 0;">
                                            <a href="%s" style="display: inline-block; padding: 14px 40px; background: linear-gradient(135deg, #FF6B6B 0%%, #FF8E8E 100%%); color: #ffffff; text-decoration: none; border-radius: 8px; font-size: 16px; font-weight: 500;">重置密码</a>
                                        </div>
                                        <p style="margin: 24px 0 0; color: #999999; font-size: 12px; line-height: 1.6;">如无法点击按钮，请复制以下链接到浏览器打开：<br>%s</p>
                                        <p style="margin: 16px 0 0; color: #999999; font-size: 12px;">如非本人操作，请忽略此邮件。</p>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 20px 40px; text-align: center; border-top: 1px solid #EEEEEE;">
                                        <p style="margin: 0; color: #999999; font-size: 12px;">小红公寓 © 2026 版权所有</p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(userName, expireHours, resetUrl, resetUrl);
    }
}
