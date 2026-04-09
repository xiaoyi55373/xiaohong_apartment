package com.xiaohong.lease.web.admin.controller.apartment;


import com.xiaohong.lease.common.annotation.CheckPermission;
import com.xiaohong.lease.common.operationlog.annotation.OperationLog;
import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.model.enums.BusinessType;
import com.xiaohong.lease.model.entity.ApartmentInfo;
import com.xiaohong.lease.model.enums.OperatorType;
import com.xiaohong.lease.model.enums.ReleaseStatus;
import com.xiaohong.lease.web.admin.service.ApartmentInfoService;
import com.xiaohong.lease.web.admin.vo.apartment.ApartmentDetailVo;
import com.xiaohong.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.xiaohong.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.xiaohong.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "公寓信息管理")
@RestController
@RequestMapping("/admin/apartment")
public class ApartmentController {

    @Autowired
    private ApartmentInfoService service;

    @CheckPermission
    @OperationLog(title = "公寓管理", businessType = BusinessType.INSERT, operatorType = OperatorType.SYSTEM_USER,
                  desc = "'保存或更新公寓: ' + #apartmentSubmitVo.name")
    @Operation(summary = "保存或更新公寓信息")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody ApartmentSubmitVo apartmentSubmitVo) {
        service.saveOrUpdateApartment(apartmentSubmitVo);
        return Result.ok();
    }

    @Operation(summary = "根据条件分页查询公寓列表")
    @GetMapping("pageItem")
    public Result<IPage<ApartmentItemVo>> pageItem(@RequestParam long current, @RequestParam long size, ApartmentQueryVo queryVo) {
        Page<ApartmentItemVo> page = new Page<>(current, size);
        IPage<ApartmentItemVo> result = service.pageItem(page, queryVo);
        return Result.ok(result);
    }

    @Operation(summary = "根据ID获取公寓详细信息")
    @GetMapping("getDetailById")
    public Result<ApartmentDetailVo> getDetailById(@RequestParam Long id) {
        ApartmentDetailVo apartmentDetailVo = service.getDetailById(id);
        return Result.ok(apartmentDetailVo);
    }

    @CheckPermission
    @OperationLog(title = "公寓管理", businessType = BusinessType.DELETE, operatorType = OperatorType.SYSTEM_USER,
                  desc = "'删除公寓ID: ' + #id")
    @Operation(summary = "根据id删除公寓信息")
    @DeleteMapping("removeById")
    public Result removeById(@RequestParam Long id) {

        service.removeApartmentById(id);
        return Result.ok();

    }

    @CheckPermission
    @OperationLog(title = "公寓管理", businessType = BusinessType.UPDATE, operatorType = OperatorType.SYSTEM_USER,
                  desc = "'修改公寓发布状态: ' + #id + ' -> ' + #status")
    @Operation(summary = "根据id修改公寓发布状态")
    @PostMapping("updateReleaseStatusById")
    public Result updateReleaseStatusById(@RequestParam Long id, @RequestParam ReleaseStatus status) {

        LambdaUpdateWrapper<ApartmentInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ApartmentInfo::getId,id);
        updateWrapper.set(ApartmentInfo::getIsRelease,status);

        service.update(updateWrapper);
        return Result.ok();
    }

    @Operation(summary = "根据区县id查询公寓信息列表")
    @GetMapping("listInfoByDistrictId")
    public Result<List<ApartmentInfo>> listInfoByDistrictId(@RequestParam Long id) {

        LambdaQueryWrapper<ApartmentInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApartmentInfo::getDistrictId,id);
        List<ApartmentInfo> list = service.list(queryWrapper);
        return Result.ok(list);
    }
}
