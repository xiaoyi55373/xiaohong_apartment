package com.xiaohong.lease.web.app.controller.mini.agreement;

import com.xiaohong.lease.common.annotation.CheckPermission;
import com.xiaohong.lease.common.context.LoginUserContext;
import com.xiaohong.lease.common.log.annotation.BehaviorLog;
import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.model.enums.BehaviorType;
import com.xiaohong.lease.web.app.service.LeaseAgreementService;
import com.xiaohong.lease.web.app.vo.agreement.AgreementDetailVo;
import com.xiaohong.lease.web.app.vo.agreement.AgreementItemVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序端租约信息控制器
 *
 * @author 小红
 * @createDate 2026-04-07
 */
@RestController
@Tag(name = "小程序-租约信息", description = "小程序端租约相关接口")
@RequestMapping("/mini/agreement")
public class MiniAgreementController {

    @Autowired
    private LeaseAgreementService leaseAgreementService;

    /**
     * 获取个人租约列表
     */
    @Operation(summary = "获取个人租约列表", description = "获取当前登录用户的租约列表")
    @BehaviorLog(value = BehaviorType.BROWSE, desc = "小程序获取个人租约列表")
    @CheckPermission
    @GetMapping("/listItem")
    public Result<List<AgreementItemVo>> listItem() {
        List<AgreementItemVo> list = leaseAgreementService.listItem(LoginUserContext.getLoginUser().getUsername());
        return Result.ok(list);
    }

    /**
     * 根据id获取租约详情
     */
    @Operation(summary = "获取租约详情", description = "根据id获取租约详细信息")
    @BehaviorLog(value = BehaviorType.BROWSE, desc = "小程序浏览租约详情", targetType = "agreement", targetId = "#id")
    @CheckPermission
    @GetMapping("/getDetailById")
    public Result<AgreementDetailVo> getDetailById(@RequestParam Long id) {
        AgreementDetailVo detail = leaseAgreementService.getDetailById(id);
        return Result.ok(detail);
    }
}
