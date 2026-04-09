package com.xiaohong.lease.web.app.controller.agreement;


import com.xiaohong.lease.common.annotation.CheckPermission;
import com.xiaohong.lease.common.context.LoginUserContext;
import com.xiaohong.lease.common.log.annotation.BehaviorLog;
import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.model.entity.LeaseAgreement;
import com.xiaohong.lease.model.enums.BehaviorType;
import com.xiaohong.lease.model.enums.LeaseStatus;
import com.xiaohong.lease.web.app.service.LeaseAgreementService;
import com.xiaohong.lease.web.app.vo.agreement.AgreementDetailVo;
import com.xiaohong.lease.web.app.vo.agreement.AgreementItemVo;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/agreement")
@Tag(name = "租约信息")
public class LeaseAgreementController {

    @Autowired
    private LeaseAgreementService service;

    @Operation(summary = "获取个人租约基本信息列表")
    @BehaviorLog(value = BehaviorType.BROWSE, desc = "查询租约列表", targetType = "agreement")
    @GetMapping("listItem")
    public Result<List<AgreementItemVo>> listItem() {
        List<AgreementItemVo> list = service.listItem(LoginUserContext.getLoginUser().getUsername());
        return Result.ok(list);
    }

    @Operation(summary = "根据id获取租约详细信息")
    @BehaviorLog(value = BehaviorType.BROWSE, desc = "查询租约详情", targetType = "agreement", targetId = "#id")
    @GetMapping("getDetailById")
    public Result<AgreementDetailVo> getDetailById(@RequestParam Long id) {
        AgreementDetailVo agreementDetailVo = service.getDetailById(id);
        return Result.ok(agreementDetailVo);
    }

    @CheckPermission
    @Operation(summary = "根据id更新租约状态", description = "用于确认租约和提前退租")
    @BehaviorLog(value = BehaviorType.SIGN, desc = "更新租约状态", targetType = "agreement", targetId = "#id")
    @PostMapping("updateStatusById")
    public Result updateStatusById(@RequestParam Long id, @RequestParam LeaseStatus leaseStatus) {
        LambdaUpdateWrapper<LeaseAgreement> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(LeaseAgreement::getId, id);
        updateWrapper.set(LeaseAgreement::getStatus, leaseStatus);
        service.update(updateWrapper);
        return Result.ok();
    }

    @CheckPermission
    @Operation(summary = "保存或更新租约", description = "用于续约")
    @BehaviorLog(value = BehaviorType.SIGN, desc = "保存或更新租约", targetType = "agreement")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody LeaseAgreement leaseAgreement) {
        service.saveOrUpdate(leaseAgreement);
        return Result.ok();
    }
}