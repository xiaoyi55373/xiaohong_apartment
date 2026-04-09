package com.xiaohong.lease.web.app.controller.apartment;

import com.xiaohong.lease.common.log.annotation.BehaviorLog;
import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.model.enums.BehaviorType;
import com.xiaohong.lease.web.app.service.ApartmentInfoService;
import com.xiaohong.lease.web.app.vo.apartment.ApartmentDetailVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "公寓信息")
@RequestMapping("/app/apartment")
public class ApartmentController {

    @Autowired
    private ApartmentInfoService service;

    @Operation(summary = "根据id获取公寓信息")
    @BehaviorLog(value = BehaviorType.BROWSE, desc = "浏览公寓详情", targetType = "apartment", targetId = "#id")
    @GetMapping("getDetailById")
    public Result<ApartmentDetailVo> getDetailById(@RequestParam Long id) {
        ApartmentDetailVo apartmentDetailVo = service.getDetailById(id);
        return Result.ok(apartmentDetailVo);
    }


}
