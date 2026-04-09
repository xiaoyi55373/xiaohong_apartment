package com.xiaohong.lease.web.admin.service;

import com.xiaohong.lease.model.vo.schedule.AppointmentExpireVo;
import com.xiaohong.lease.model.vo.schedule.TaskExecuteResult;

import java.util.List;

/**
 * 预约过期处理服务
 *
 * @author 小红
 */
public interface AppointmentExpireService {

    /**
     * 查询已过期的预约
     * @return 过期预约列表
     */
    List<AppointmentExpireVo> queryExpiredAppointments();

    /**
     * 处理过期预约
     * @param appointmentList 预约列表
     * @return 处理结果
     */
    TaskExecuteResult processExpiredAppointments(List<AppointmentExpireVo> appointmentList);

    /**
     * 执行预约过期处理任务
     * @return 执行结果
     */
    TaskExecuteResult executeExpireTask();

    /**
     * 发送预约过期通知
     */
    boolean sendExpireNotification(AppointmentExpireVo appointment);
}
