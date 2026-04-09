package com.xiaohong.lease.web.app.controller.appointment;


import com.xiaohong.lease.common.annotation.CheckPermission;
import com.xiaohong.lease.common.context.LoginUserContext;
import com.xiaohong.lease.common.log.annotation.BehaviorLog;
import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.model.entity.ViewAppointment;
import com.xiaohong.lease.model.enums.BehaviorType;
import com.xiaohong.lease.web.app.service.ViewAppointmentService;
import com.xiaohong.lease.web.app.vo.appointment.AppointmentDetailVo;
import com.xiaohong.lease.web.app.vo.appointment.AppointmentItemVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "看房预约信息")
@RestController
@RequestMapping("/app/appointment")
public class ViewAppointmentController {

    @Autowired
    private ViewAppointmentService service;

    @CheckPermission
    @Operation(summary = "保存或更新看房预约")
    @BehaviorLog(value = BehaviorType.APPOINTMENT, desc = "预约看房", targetType = "appointment")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody ViewAppointment viewAppointment) {
        viewAppointment.setUserId(LoginUserContext.getLoginUser().getUserId());
        service.saveOrUpdate(viewAppointment);
        return Result.ok();
    }

    @Operation(summary = "查询个人预约看房列表")
    @BehaviorLog(value = BehaviorType.BROWSE, desc = "查询预约列表", targetType = "appointment")
    @GetMapping("listItem")
    public Result<List<AppointmentItemVo>> listItem() {
        List<AppointmentItemVo> list = service.listItem(LoginUserContext.getLoginUser().getUserId());
        return Result.ok(list);
    }


    @GetMapping("getDetailById")
    @Operation(summary = "根据ID查询预约详情信息")
    @BehaviorLog(value = BehaviorType.BROWSE, desc = "查询预约详情", targetType = "appointment", targetId = "#id")
    public Result<AppointmentDetailVo> getDetailById(Long id) {
        AppointmentDetailVo appointmentDetailVo = service.getDetailById(id);
        return Result.ok(appointmentDetailVo);
    }

}

