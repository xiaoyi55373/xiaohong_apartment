package com.xiaohong.lease.web.app.controller.room;


import com.xiaohong.lease.common.log.annotation.BehaviorLog;
import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.model.enums.BehaviorType;
import com.xiaohong.lease.web.app.service.RoomInfoService;
import com.xiaohong.lease.web.app.vo.room.RoomDetailVo;
import com.xiaohong.lease.web.app.vo.room.RoomItemVo;
import com.xiaohong.lease.web.app.vo.room.RoomQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "房间信息")
@RestController
@RequestMapping("/app/room")
public class RoomController {

    @Autowired
    RoomInfoService service;

    @Operation(summary = "分页查询房间列表")
    @BehaviorLog(value = BehaviorType.SEARCH, desc = "搜索房间列表", targetType = "room")
    @GetMapping("pageItem")
    public Result<IPage<RoomItemVo>> pageItem(@RequestParam long current, @RequestParam long size, RoomQueryVo queryVo) {
        IPage<RoomItemVo> page = new Page<>(current, size);
        IPage<RoomItemVo> list = service.pageItem(page, queryVo);
        return Result.ok(list);
    }

    @Operation(summary = "根据id获取房间的详细信息")
    @BehaviorLog(value = BehaviorType.BROWSE, desc = "浏览房间详情", targetType = "room", targetId = "#id")
    @GetMapping("getDetailById")
    public Result<RoomDetailVo> getDetailById(@RequestParam Long id) {
        RoomDetailVo roomDetailVo = service.getDetailById(id);
        return Result.ok(roomDetailVo);
    }

    @Operation(summary = "根据公寓id分页查询房间列表")
    @BehaviorLog(value = BehaviorType.BROWSE, desc = "浏览公寓房间列表", targetType = "apartment", targetId = "#id")
    @GetMapping("pageItemByApartmentId")
    public Result<IPage<RoomItemVo>> pageItemByApartmentId(@RequestParam long current, @RequestParam long size, @RequestParam Long id) {
        Page<RoomItemVo> page = new Page<>(current, size);
        IPage<RoomItemVo> list = service.pageItemByApartmentId(page, id);
        return Result.ok(list);
    }

}
