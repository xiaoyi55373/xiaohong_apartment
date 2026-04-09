package com.xiaohong.lease.web.admin.controller.apartment;


import com.xiaohong.lease.common.annotation.CheckPermission;
import com.xiaohong.lease.common.operationlog.annotation.OperationLog;
import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.model.enums.BusinessType;
import com.xiaohong.lease.model.entity.RoomInfo;
import com.xiaohong.lease.model.enums.OperatorType;
import com.xiaohong.lease.model.enums.ReleaseStatus;
import com.xiaohong.lease.web.admin.service.RoomInfoService;
import com.xiaohong.lease.web.admin.vo.room.RoomDetailVo;
import com.xiaohong.lease.web.admin.vo.room.RoomItemVo;
import com.xiaohong.lease.web.admin.vo.room.RoomQueryVo;
import com.xiaohong.lease.web.admin.vo.room.RoomSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "房间信息管理")
@RestController
@RequestMapping("/admin/room")
public class RoomController {

    @Autowired
    private RoomInfoService service;

    @CheckPermission
    @OperationLog(title = "房间管理", businessType = BusinessType.INSERT, operatorType = OperatorType.SYSTEM_USER,
                  desc = "'保存或更新房间: ' + #roomSubmitVo.roomNumber")
    @Operation(summary = "保存或更新房间信息")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody RoomSubmitVo roomSubmitVo) {
        service.saveOrUpdateRoom(roomSubmitVo);
        return Result.ok();
    }

    @Operation(summary = "根据条件分页查询房间列表")
    @GetMapping("pageItem")
    public Result<IPage<RoomItemVo>> pageItem(@RequestParam long current, @RequestParam long size, RoomQueryVo queryVo) {
        IPage<RoomItemVo> page = new Page<>(current, size);
        IPage<RoomItemVo> list = service.pageItem(page,queryVo);
        return Result.ok(list);
    }

    @Operation(summary = "根据id获取房间详细信息")
    @GetMapping("getDetailById")
    public Result<RoomDetailVo> getDetailById(@RequestParam Long id) {
        RoomDetailVo room = service.getRoomById(id);
        return Result.ok(room);
    }

    @CheckPermission
    @OperationLog(title = "房间管理", businessType = BusinessType.DELETE, operatorType = OperatorType.SYSTEM_USER,
                  desc = "'删除房间ID: ' + #id")
    @Operation(summary = "根据id删除房间信息")
    @DeleteMapping("removeById")
    public Result removeById(@RequestParam Long id) {
        service.removeRoomById(id);
        return Result.ok();
    }

    @CheckPermission
    @OperationLog(title = "房间管理", businessType = BusinessType.UPDATE, operatorType = OperatorType.SYSTEM_USER,
                  desc = "'修改房间发布状态: ' + #id + ' -> ' + #status")
    @Operation(summary = "根据id修改房间发布状态")
    @PostMapping("updateReleaseStatusById")
    public Result updateReleaseStatusById(Long id, ReleaseStatus status) {
        LambdaUpdateWrapper<RoomInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(RoomInfo::getId, id);
        updateWrapper.set(RoomInfo::getIsRelease, status);
        service.update(updateWrapper);
        return Result.ok();
    }

    @GetMapping("listBasicByApartmentId")
    @Operation(summary = "根据公寓id查询房间列表")
    public Result<List<RoomInfo>> listBasicByApartmentId(Long id) {
        LambdaQueryWrapper<RoomInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoomInfo::getApartmentId, id);
        queryWrapper.eq(RoomInfo::getIsRelease, ReleaseStatus.RELEASED);
        List<RoomInfo> roomInfoList = service.list(queryWrapper);
        return Result.ok(roomInfoList);
    }

}
