package com.xiaohong.lease.web.admin.service.impl;

import com.xiaohong.lease.common.mail.MailService;
import com.xiaohong.lease.model.entity.LeaseAgreement;
import com.xiaohong.lease.model.enums.LeaseStatus;
import com.xiaohong.lease.model.vo.schedule.LeaseExpireRemindVo;
import com.xiaohong.lease.model.vo.schedule.TaskExecuteResult;
import com.xiaohong.lease.web.admin.mapper.ApartmentInfoMapper;
import com.xiaohong.lease.web.admin.mapper.LeaseAgreementMapper;
import com.xiaohong.lease.web.admin.mapper.RoomInfoMapper;
import com.xiaohong.lease.web.admin.service.LeaseExpireRemindService;
import com.xiaohong.lease.common.sms.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 租约到期提醒服务实现
 *
 * @author 小红
 */
@Service
public class LeaseExpireRemindServiceImpl implements LeaseExpireRemindService {

    private static final Logger log = LoggerFactory.getLogger(LeaseExpireRemindServiceImpl.class);

    @Autowired
    private LeaseAgreementMapper leaseAgreementMapper;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Autowired
    private SmsService smsService;

    @Autowired
    private MailService mailService;

    @Override
    public List<LeaseExpireRemindVo> queryExpiringLeases(Integer daysBefore) {
        // 计算目标日期
        Date targetDate = new Date(System.currentTimeMillis() + daysBefore * 24 * 60 * 60 * 1000L);
        
        // 查询即将到期的租约
        List<LeaseAgreement> agreements = leaseAgreementMapper.selectExpiringLeases(
            targetDate, 
            List.of(LeaseStatus.SIGNED, LeaseStatus.WITHDRAWING)
        );
        
        List<LeaseExpireRemindVo> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (LeaseAgreement agreement : agreements) {
            LeaseExpireRemindVo vo = new LeaseExpireRemindVo();
            vo.setLeaseId(agreement.getId());
            vo.setName(agreement.getName());
            vo.setPhone(agreement.getPhone());
            vo.setLeaseStartDate(agreement.getLeaseStartDate());
            vo.setLeaseEndDate(agreement.getLeaseEndDate());
            vo.setRent(agreement.getRent());
            vo.setDeposit(agreement.getDeposit());
            
            // 计算剩余天数
            long diffMillis = agreement.getLeaseEndDate().getTime() - System.currentTimeMillis();
            int daysRemaining = (int) Math.ceil(diffMillis / (24 * 60 * 60 * 1000.0));
            vo.setDaysRemaining(daysRemaining);
            
            // 查询公寓信息
            if (agreement.getApartmentId() != null) {
                vo.setApartmentId(agreement.getApartmentId());
                // 这里简化处理，实际需要查询公寓信息
            }
            
            // 查询房间信息
            if (agreement.getRoomId() != null) {
                vo.setRoomId(agreement.getRoomId());
                // 这里简化处理，实际需要查询房间信息
            }
            
            result.add(vo);
        }
        
        return result;
    }

    @Override
    public TaskExecuteResult sendReminders(List<LeaseExpireRemindVo> remindList, String notifyType) {
        if (remindList == null || remindList.isEmpty()) {
            return TaskExecuteResult.success("没有需要提醒的租约", 0);
        }
        
        AtomicInteger smsCount = new AtomicInteger(0);
        AtomicInteger emailCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        
        for (LeaseExpireRemindVo remind : remindList) {
            boolean smsSuccess = true;
            boolean emailSuccess = true;
            
            // 发送短信
            if ("SMS".equals(notifyType) || "BOTH".equals(notifyType)) {
                if (remind.getPhone() != null && !remind.getPhone().isEmpty()) {
                    smsSuccess = sendSmsReminder(remind);
                    if (smsSuccess) {
                        smsCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                }
            }
            
            // 发送邮件
            if ("EMAIL".equals(notifyType) || "BOTH".equals(notifyType)) {
                if (remind.getEmail() != null && !remind.getEmail().isEmpty()) {
                    emailSuccess = sendEmailReminder(remind);
                    if (emailSuccess) {
                        emailCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                }
            }
            
            // 稍微延迟，避免发送过快
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        String message = String.format("租约到期提醒发送完成：共 %d 条，短信 %d 条，邮件 %d 条，失败 %d 条",
                remindList.size(), smsCount.get(), emailCount.get(), failCount.get());
        
        return TaskExecuteResult.success(message, remindList.size(), 
                String.format("SMS:%d, EMAIL:%d, FAIL:%d", smsCount.get(), emailCount.get(), failCount.get()));
    }

    @Override
    public TaskExecuteResult executeRemindTask(Integer remindDaysBefore, String notifyType) {
        log.info("执行租约到期提醒任务，提前 {} 天，通知方式: {}", remindDaysBefore, notifyType);
        
        try {
            // 查询即将到期的租约
            List<LeaseExpireRemindVo> remindList = queryExpiringLeases(remindDaysBefore);
            
            if (remindList.isEmpty()) {
                log.info("没有需要提醒的租约");
                return TaskExecuteResult.success("没有需要提醒的租约", 0);
            }
            
            log.info("查询到 {} 条即将到期的租约", remindList.size());
            
            // 发送提醒
            return sendReminders(remindList, notifyType);
            
        } catch (Exception e) {
            log.error("租约到期提醒任务执行失败", e);
            return TaskExecuteResult.fail("任务执行失败: " + e.getMessage());
        }
    }

    @Override
    public boolean sendSmsReminder(LeaseExpireRemindVo remind) {
        try {
            // 构建短信内容
            String content = String.format("【小红公寓】尊敬的 %s，您的租约将于 %d 天后到期（%s），请及时办理续约或退租手续。",
                    remind.getName(),
                    remind.getDaysRemaining(),
                    new SimpleDateFormat("yyyy-MM-dd").format(remind.getLeaseEndDate()));
            
            // 调用短信服务
            // smsService.sendMessage(remind.getPhone(), content);
            
            log.info("发送租约到期短信提醒: {} -> {}", remind.getPhone(), content);
            return true;
        } catch (Exception e) {
            log.error("发送短信提醒失败: {}", remind.getPhone(), e);
            return false;
        }
    }

    @Override
    public boolean sendEmailReminder(LeaseExpireRemindVo remind) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String expireDate = sdf.format(remind.getLeaseEndDate());
            
            mailService.sendLeaseExpirationReminderMail(
                    remind.getEmail(),
                    remind.getName(),
                    remind.getApartmentName() != null ? remind.getApartmentName() : "小红公寓",
                    expireDate,
                    remind.getDaysRemaining()
            );
            
            log.info("发送租约到期邮件提醒: {}", remind.getEmail());
            return true;
        } catch (Exception e) {
            log.error("发送邮件提醒失败: {}", remind.getEmail(), e);
            return false;
        }
    }
}
