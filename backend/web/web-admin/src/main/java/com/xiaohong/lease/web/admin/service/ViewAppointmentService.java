package com.xiaohong.lease.web.admin.service;

import com.xiaohong.lease.model.entity.ViewAppointment;
import com.xiaohong.lease.model.enums.AppointmentStatus;
import com.xiaohong.lease.web.admin.vo.appointment.AppointmentQueryVo;
import com.xiaohong.lease.web.admin.vo.appointment.AppointmentVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 小红
* @description 针对表【view_appointment(预约看房信息表)】的数据库操作Service
* @createDate 2023-07-24 15:48:00
*/
public interface ViewAppointmentService extends IService<ViewAppointment> {

    IPage<AppointmentVo> pageItem(Page<AppointmentVo> page, AppointmentQueryVo queryVo);

    /**
     * 查询所有预约信息（用于导出）
     * @param queryVo 查询条件
     * @return 预约列表
     */
    List<AppointmentVo> listAll(AppointmentQueryVo queryVo);

    /**
     * 根据 ID 更新预约状态，并发送微信订阅消息通知用户
     * @param id 预约 ID
     * @param status 目标状态
     * @return 是否更新成功
     */
    boolean updateStatusByIdWithNotify(Long id, AppointmentStatus status);
}
