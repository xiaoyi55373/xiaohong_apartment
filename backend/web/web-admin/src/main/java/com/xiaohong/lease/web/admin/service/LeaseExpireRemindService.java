package com.xiaohong.lease.web.admin.service;

import com.xiaohong.lease.model.vo.schedule.LeaseExpireRemindVo;
import com.xiaohong.lease.model.vo.schedule.TaskExecuteResult;

import java.util.List;

/**
 * 租约到期提醒服务
 *
 * @author 小红
 */
public interface LeaseExpireRemindService {

    /**
     * 查询即将到期的租约
     * @param daysBefore 提前天数
     * @return 即将到期的租约列表
     */
    List<LeaseExpireRemindVo> queryExpiringLeases(Integer daysBefore);

    /**
     * 发送租约到期提醒
     * @param remindList 提醒列表
     * @param notifyType 通知方式：SMS-短信, EMAIL-邮件, BOTH-两者
     * @return 发送结果
     */
    TaskExecuteResult sendReminders(List<LeaseExpireRemindVo> remindList, String notifyType);

    /**
     * 执行租约到期提醒任务
     * @param remindDaysBefore 提前提醒天数
     * @param notifyType 通知方式
     * @return 执行结果
     */
    TaskExecuteResult executeRemindTask(Integer remindDaysBefore, String notifyType);

    /**
     * 发送短信提醒
     */
    boolean sendSmsReminder(LeaseExpireRemindVo remind);

    /**
     * 发送邮件提醒
     */
    boolean sendEmailReminder(LeaseExpireRemindVo remind);
}
