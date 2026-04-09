package com.xiaohong.lease.web.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xiaohong.lease.model.entity.ViewAppointment;
import com.xiaohong.lease.model.enums.AppointmentStatus;
import com.xiaohong.lease.model.vo.schedule.AppointmentExpireVo;
import com.xiaohong.lease.model.vo.schedule.TaskExecuteResult;
import com.xiaohong.lease.web.admin.mapper.ViewAppointmentMapper;
import com.xiaohong.lease.web.admin.service.AppointmentExpireService;
import com.xiaohong.lease.common.sms.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 预约过期处理服务实现
 *
 * @author 小红
 */
@Service
public class AppointmentExpireServiceImpl implements AppointmentExpireService {

    private static final Logger log = LoggerFactory.getLogger(AppointmentExpireServiceImpl.class);

    @Autowired
    private ViewAppointmentMapper viewAppointmentMapper;

    @Autowired
    private SmsService smsService;

    @Override
    public List<AppointmentExpireVo> queryExpiredAppointments() {
        // 查询已过期的待看房预约（预约时间已过24小时）
        Date expireTime = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000L);
        
        List<ViewAppointment> appointments = viewAppointmentMapper.selectExpiredAppointments(
            AppointmentStatus.WAITING,
            expireTime
        );
        
        List<AppointmentExpireVo> result = new ArrayList<>();
        for (ViewAppointment appointment : appointments) {
            AppointmentExpireVo vo = new AppointmentExpireVo();
            vo.setAppointmentId(appointment.getId());
            vo.setUserId(appointment.getUserId());
            vo.setName(appointment.getName());
            vo.setPhone(appointment.getPhone());
            vo.setApartmentId(appointment.getApartmentId());
            vo.setAppointmentTime(appointment.getAppointmentTime());
            vo.setAppointmentStatus(appointment.getAppointmentStatus().getName());
            vo.setIsExpired(true);
            result.add(vo);
        }
        
        return result;
    }

    @Override
    public TaskExecuteResult processExpiredAppointments(List<AppointmentExpireVo> appointmentList) {
        if (appointmentList == null || appointmentList.isEmpty()) {
            return TaskExecuteResult.success("没有需要处理的过期预约", 0);
        }
        
        AtomicInteger processedCount = new AtomicInteger(0);
        AtomicInteger notifyCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        
        for (AppointmentExpireVo appointment : appointmentList) {
            try {
                // 更新预约状态为已过期
                LambdaUpdateWrapper<ViewAppointment> wrapper = new LambdaUpdateWrapper<>();
                wrapper.eq(ViewAppointment::getId, appointment.getAppointmentId())
                       .set(ViewAppointment::getAppointmentStatus, AppointmentStatus.EXPIRED);
                
                int updated = viewAppointmentMapper.update(null, wrapper);
                
                if (updated > 0) {
                    processedCount.incrementAndGet();
                    
                    // 发送过期通知
                    if (sendExpireNotification(appointment)) {
                        notifyCount.incrementAndGet();
                    }
                    
                    log.info("预约已标记为过期: ID={}", appointment.getAppointmentId());
                }
            } catch (Exception e) {
                failCount.incrementAndGet();
                log.error("处理过期预约失败: ID={}", appointment.getAppointmentId(), e);
            }
        }
        
        String message = String.format("预约过期处理完成：共 %d 条，处理成功 %d 条，通知 %d 条，失败 %d 条",
                appointmentList.size(), processedCount.get(), notifyCount.get(), failCount.get());
        
        return TaskExecuteResult.success(message, processedCount.get(),
                String.format("PROCESSED:%d, NOTIFIED:%d, FAIL:%d", processedCount.get(), notifyCount.get(), failCount.get()));
    }

    @Override
    public TaskExecuteResult executeExpireTask() {
        log.info("执行预约过期处理任务");
        
        try {
            // 查询已过期的预约
            List<AppointmentExpireVo> expiredList = queryExpiredAppointments();
            
            if (expiredList.isEmpty()) {
                log.info("没有需要处理的过期预约");
                return TaskExecuteResult.success("没有需要处理的过期预约", 0);
            }
            
            log.info("查询到 {} 条过期预约", expiredList.size());
            
            // 处理过期预约
            return processExpiredAppointments(expiredList);
            
        } catch (Exception e) {
            log.error("预约过期处理任务执行失败", e);
            return TaskExecuteResult.fail("任务执行失败: " + e.getMessage());
        }
    }

    @Override
    public boolean sendExpireNotification(AppointmentExpireVo appointment) {
        try {
            // 构建通知内容
            String content = String.format("【小红公寓】尊敬的 %s，您预约的看房因超时未到访已自动取消，如需看房请重新预约。",
                    appointment.getName());
            
            // 调用短信服务
            if (appointment.getPhone() != null && !appointment.getPhone().isEmpty()) {
                // smsService.sendMessage(appointment.getPhone(), content);
                log.info("发送预约过期通知: {} -> {}", appointment.getPhone(), content);
            }
            
            return true;
        } catch (Exception e) {
            log.error("发送过期通知失败: {}", appointment.getPhone(), e);
            return false;
        }
    }
}
