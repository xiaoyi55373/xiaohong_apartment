package com.xiaohong.lease.web.app.controller.mini.appointment;

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

/**
 * 小程序端预约看房控制器
 *
 * @author 小红
 * @createDate 2026-04-07
 */
@RestController
@Tag(name = "小程序-预约看房", description = "小程序端预约看房相关接口")
@RequestMapping("/mini/appointment")
public class MiniAppointmentController {

    @Autowired
    private ViewAppointmentService service;

    @CheckPermission
    @Operation(summary = "保存或更新预约", description = "保存或更新看房预约信息")
    @BehaviorLog(value = BehaviorType.APPOINTMENT, desc = "小程序预约看房", targetType = "appointment")
    @PostMapping("/saveOrUpdate")
    public Result saveOrUpdate(@RequestBody ViewAppointment viewAppointment) {
        viewAppointment.setUserId(LoginUserContext.getLoginUser().getUserId());
        service.saveOrUpdate(viewAppointment);
        return Result.ok();
    }

    @CheckPermission
    @Operation(summary = "查询我的预约列表", description = "查询当前登录用户的看房预约列表")
    @BehaviorLog(value = BehaviorType.BROWSE, desc = "小程序查询预约列表", targetType = "appointment")
    @GetMapping("/listItem")
    public Result<List<AppointmentItemVo>> listItem() {
        List<AppointmentItemVo> list = service.listItem(LoginUserContext.getLoginUser().getUserId());
        return Result.ok(list);
    }

    @CheckPermission
    @Operation(summary = "查询预约详情", description = "根据ID查询预约详情信息")
    @BehaviorLog(value = BehaviorType.BROWSE, desc = "小程序查询预约详情", targetType = "appointment", targetId = "#id")
    @GetMapping("/getDetailById")
    public Result<AppointmentDetailVo> getDetailById(@RequestParam Long id) {
        AppointmentDetailVo detail = service.getDetailById(id);
        return Result.ok(detail);
    }
}
