package com.xiaohong.lease.web.app.controller.mini.room;

import com.xiaohong.lease.common.log.annotation.BehaviorLog;
import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.model.entity.FacilityInfo;
import com.xiaohong.lease.model.entity.LabelInfo;
import com.xiaohong.lease.model.enums.BehaviorType;
import com.xiaohong.lease.web.app.service.RoomInfoService;
import com.xiaohong.lease.web.app.vo.graph.GraphVo;
import com.xiaohong.lease.web.app.vo.room.RoomDetailVo;
import com.xiaohong.lease.web.app.vo.room.RoomItemVo;
import com.xiaohong.lease.web.app.vo.room.RoomQueryVo;
import com.xiaohong.lease.web.app.vo.mini.room.MiniRoomDetailVo;
import com.xiaohong.lease.web.app.vo.mini.room.MiniRoomItemVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 小程序端房间信息控制器
 *
 * @author 小红
 * @createDate 2026-04-07
 */
@RestController
@Tag(name = "小程序-房间信息", description = "小程序端房间信息相关接口")
@RequestMapping("/mini/room")
public class MiniRoomController {

    @Autowired
    private RoomInfoService roomInfoService;

    /**
     * 分页查询房间列表（适配小程序展示）
     */
    @Operation(summary = "分页查询房间列表", description = "分页查询房间列表，返回字段适配小程序端展示")
    @BehaviorLog(value = BehaviorType.SEARCH, desc = "小程序搜索房间列表", targetType = "room")
    @GetMapping("/pageItem")
    public Result<IPage<MiniRoomItemVo>> pageItem(
            @Parameter(description = "当前页") @RequestParam long current,
            @Parameter(description = "每页大小") @RequestParam long size,
            RoomQueryVo queryVo) {

        IPage<RoomItemVo> page = new Page<>(current, size);
        IPage<RoomItemVo> resultPage = roomInfoService.pageItem(page, queryVo);

        // 转换为小程序VO
        List<MiniRoomItemVo> miniList = resultPage.getRecords().stream()
                .map(this::convertToMiniRoomItemVo)
                .collect(Collectors.toList());

        IPage<MiniRoomItemVo> miniPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        miniPage.setRecords(miniList);

        return Result.ok(miniPage);
    }

    /**
     * 根据id获取房间详情（适配小程序展示）
     */
    @Operation(summary = "获取房间详情", description = "根据id获取房间详细信息，返回字段适配小程序端展示")
    @BehaviorLog(value = BehaviorType.BROWSE, desc = "小程序浏览房间详情", targetType = "room", targetId = "#id")
    @GetMapping("/getDetailById")
    public Result<MiniRoomDetailVo> getDetailById(
            @Parameter(description = "房间ID") @RequestParam Long id) {

        RoomDetailVo detailVo = roomInfoService.getDetailById(id);
        if (detailVo == null) {
            return Result.ok();
        }

        MiniRoomDetailVo miniVo = new MiniRoomDetailVo();
        BeanUtils.copyProperties(detailVo, miniVo);
        miniVo.setId(detailVo.getId());
        miniVo.setRoomNumber(detailVo.getRoomNumber());
        miniVo.setRent(detailVo.getRent());
        miniVo.setIsCheckIn(detailVo.getIsCheckIn());

        // 公寓信息裁剪
        if (detailVo.getApartmentItemVo() != null) {
            miniVo.setApartmentId(detailVo.getApartmentItemVo().getId());
            miniVo.setApartmentName(detailVo.getApartmentItemVo().getName());
            miniVo.setAddressDetail(detailVo.getApartmentItemVo().getAddressDetail());
            miniVo.setLatitude(detailVo.getApartmentItemVo().getLatitude());
            miniVo.setLongitude(detailVo.getApartmentItemVo().getLongitude());
            miniVo.setPhone(detailVo.getApartmentItemVo().getPhone());
        }

        // 图片列表裁剪
        List<GraphVo> graphVoList = detailVo.getGraphVoList();
        if (graphVoList != null) {
            miniVo.setImageList(graphVoList.stream()
                    .map(GraphVo::getUrl)
                    .collect(Collectors.toList()));
        }

        // 标签名称列表裁剪
        List<LabelInfo> labelInfoList = detailVo.getLabelInfoList();
        if (labelInfoList != null) {
            miniVo.setLabelNames(labelInfoList.stream()
                    .map(LabelInfo::getName)
                    .collect(Collectors.toList()));
        }

        // 配套名称列表裁剪
        List<FacilityInfo> facilityInfoList = detailVo.getFacilityInfoList();
        if (facilityInfoList != null) {
            miniVo.setFacilityNames(facilityInfoList.stream()
                    .map(FacilityInfo::getName)
                    .collect(Collectors.toList()));
        }

        miniVo.setAttrValueVoList(detailVo.getAttrValueVoList());
        miniVo.setPaymentTypeList(detailVo.getPaymentTypeList());
        miniVo.setFeeValueVoList(detailVo.getFeeValueVoList());
        miniVo.setLeaseTermList(detailVo.getLeaseTermList());

        return Result.ok(miniVo);
    }

    /**
     * 根据公寓id分页查询房间列表（适配小程序展示）
     */
    @Operation(summary = "根据公寓ID查询房间列表", description = "根据公寓id分页查询房间列表，返回字段适配小程序端展示")
    @BehaviorLog(value = BehaviorType.BROWSE, desc = "小程序浏览公寓房间列表", targetType = "apartment", targetId = "#id")
    @GetMapping("/pageItemByApartmentId")
    public Result<IPage<MiniRoomItemVo>> pageItemByApartmentId(
            @Parameter(description = "当前页") @RequestParam long current,
            @Parameter(description = "每页大小") @RequestParam long size,
            @Parameter(description = "公寓ID") @RequestParam Long id) {

        Page<RoomItemVo> page = new Page<>(current, size);
        IPage<RoomItemVo> resultPage = roomInfoService.pageItemByApartmentId(page, id);

        // 转换为小程序VO
        List<MiniRoomItemVo> miniList = resultPage.getRecords().stream()
                .map(this::convertToMiniRoomItemVo)
                .collect(Collectors.toList());

        IPage<MiniRoomItemVo> miniPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        miniPage.setRecords(miniList);

        return Result.ok(miniPage);
    }

    /**
     * 将 RoomItemVo 转换为 MiniRoomItemVo
     */
    private MiniRoomItemVo convertToMiniRoomItemVo(RoomItemVo itemVo) {
        MiniRoomItemVo miniVo = new MiniRoomItemVo();
        miniVo.setId(itemVo.getId());
        miniVo.setRoomNumber(itemVo.getRoomNumber());
        miniVo.setRent(itemVo.getRent());

        // 首图
        if (itemVo.getGraphVoList() != null && !itemVo.getGraphVoList().isEmpty()) {
            miniVo.setFirstImage(itemVo.getGraphVoList().get(0).getUrl());
        }

        // 标签名称列表
        if (itemVo.getLabelInfoList() != null) {
            miniVo.setLabelNames(itemVo.getLabelInfoList().stream()
                    .map(LabelInfo::getName)
                    .collect(Collectors.toList()));
        }

        // 公寓信息裁剪
        if (itemVo.getApartmentInfo() != null) {
            miniVo.setApartmentId(itemVo.getApartmentInfo().getId());
            miniVo.setApartmentName(itemVo.getApartmentInfo().getName());
            miniVo.setDistrictName(itemVo.getApartmentInfo().getDistrictName());
            miniVo.setCityName(itemVo.getApartmentInfo().getCityName());
        }

        return miniVo;
    }
}
