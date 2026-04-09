package com.xiaohong.lease.web.admin.schedule;

import com.xiaohong.lease.model.entity.LeaseAgreement;
import com.xiaohong.lease.model.entity.ScheduledTask;
import com.xiaohong.lease.model.enums.LeaseStatus;
import com.xiaohong.lease.web.admin.service.LeaseAgreementService;
import com.xiaohong.lease.web.admin.service.ScheduledTaskService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

/**
 * 系统定时任务
 * 
 * 包含：
 * 1. 租约状态检查（每日凌晨执行）
 * 2. 配置的定时任务执行
 *
 * @author 小红
 */
@Component
public class ScheduleTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduleTasks.class);

    @Autowired
    private LeaseAgreementService leaseAgreementService;

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @Autowired
    private ScheduleTaskExecutor taskExecutor;

    /**
     * 初始化定时任务配置
     */
    @PostConstruct
    public void init() {
        log.info("初始化定时任务配置...");
        scheduledTaskService.initDefaultTasks();
    }

    /**
     * 租约状态检查
     * 每天凌晨0点执行
     * 检查已到期租约并更新状态
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkLeaseStatus() {
        log.info("开始执行租约状态检查任务...");
        
        try {
            // where lease_end_date <= now() and status in (2,5)
            LambdaUpdateWrapper<LeaseAgreement> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.le(LeaseAgreement::getLeaseEndDate, new Date());
            updateWrapper.in(LeaseAgreement::getStatus, LeaseStatus.SIGNED, LeaseStatus.WITHDRAWING);
            updateWrapper.set(LeaseAgreement::getStatus, LeaseStatus.EXPIRED);

            boolean success = leaseAgreementService.update(updateWrapper);
            
            if (success) {
                log.info("租约状态检查任务执行完成");
            } else {
                log.warn("租约状态检查任务执行失败或无数据更新");
            }
        } catch (Exception e) {
            log.error("租约状态检查任务执行异常", e);
        }
    }

    /**
     * 执行配置的定时任务
     * 每小时执行一次，检查并执行到期的任务
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void executeConfiguredTasks() {
        log.info("开始执行配置的定时任务...");
        
        try {
            // 获取所有启用的任务
            List<ScheduledTask> enabledTasks = scheduledTaskService.listEnabledTasks();
            
            for (ScheduledTask task : enabledTasks) {
                try {
                    taskExecutor.executeTask(task);
                } catch (Exception e) {
                    log.error("执行任务失败: {}", task.getTaskName(), e);
                }
            }
            
            log.info("配置的定时任务执行完成，共执行 {} 个任务", enabledTasks.size());
        } catch (Exception e) {
            log.error("执行配置的定时任务异常", e);
        }
    }

    /**
     * 租约到期提醒任务
     * 每天上午9点执行
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void leaseExpireRemind() {
        log.info("开始执行租约到期提醒任务...");
        
        try {
            ScheduledTask task = scheduledTaskService.getByTaskType("LEASE_EXPIRE_REMIND");
            if (task != null && task.getStatus().getCode() == 1) {
                taskExecutor.executeTask(task);
            } else {
                log.info("租约到期提醒任务未配置或已禁用");
            }
        } catch (Exception e) {
            log.error("租约到期提醒任务执行异常", e);
        }
    }

    /**
     * 预约过期处理任务
     * 每天凌晨1点执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void appointmentExpireProcess() {
        log.info("开始执行预约过期处理任务...");
        
        try {
            ScheduledTask task = scheduledTaskService.getByTaskType("APPOINTMENT_EXPIRE");
            if (task != null && task.getStatus().getCode() == 1) {
                taskExecutor.executeTask(task);
            } else {
                log.info("预约过期处理任务未配置或已禁用");
            }
        } catch (Exception e) {
            log.error("预约过期处理任务执行异常", e);
        }
    }

    /**
     * 数据清理任务
     * 每周日凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * 0")
    public void dataCleanup() {
        log.info("开始执行数据清理任务...");
        
        try {
            ScheduledTask task = scheduledTaskService.getByTaskType("DATA_CLEANUP");
            if (task != null && task.getStatus().getCode() == 1) {
                taskExecutor.executeTask(task);
            } else {
                log.info("数据清理任务未配置或已禁用");
            }
        } catch (Exception e) {
            log.error("数据清理任务执行异常", e);
        }
    }
}
