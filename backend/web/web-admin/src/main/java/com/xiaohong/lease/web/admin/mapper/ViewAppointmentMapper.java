package com.xiaohong.lease.web.admin.mapper;

import com.xiaohong.lease.model.entity.ViewAppointment;
import com.xiaohong.lease.model.enums.AppointmentStatus;
import com.xiaohong.lease.web.admin.vo.appointment.AppointmentQueryVo;
import com.xiaohong.lease.web.admin.vo.appointment.AppointmentVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
* @author 小红
* @description 针对表【view_appointment(预约看房信息表)】的数据库操作Mapper
* @createDate 2023-07-24 15:48:00
* @Entity com.xiaohong.lease.model.ViewAppointment
*/
public interface ViewAppointmentMapper extends BaseMapper<ViewAppointment> {

    IPage<AppointmentVo> pageItem(IPage<AppointmentVo> page, AppointmentQueryVo queryVo);

    /**
     * 查询已过期的预约
     * @param status 预约状态
     * @param expireTime 过期时间
     * @return 预约列表
     */
    List<ViewAppointment> selectExpiredAppointments(@Param("status") AppointmentStatus status, 
                                                     @Param("expireTime") Date expireTime);
}
