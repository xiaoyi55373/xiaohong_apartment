package com.xiaohong.lease.web.app.controller.mini.apartment;

import com.xiaohong.lease.common.log.annotation.BehaviorLog;
import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.model.entity.FacilityInfo;
import com.xiaohong.lease.model.entity.LabelInfo;
import com.xiaohong.lease.model.enums.BehaviorType;
import com.xiaohong.lease.web.app.service.ApartmentInfoService;
import com.xiaohong.lease.web.app.vo.apartment.ApartmentDetailVo;
import com.xiaohong.lease.web.app.vo.graph.GraphVo;
import com.xiaohong.lease.web.app.vo.mini.apartment.MiniApartmentDetailVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 小程序端公寓信息控制器
 *
 * @author 小红
 * @createDate 2026-04-07
 */
@RestController
@Tag(name = "小程序-公寓信息", description = "小程序端公寓信息相关接口")
@RequestMapping("/mini/apartment")
public class MiniApartmentController {

    @Autowired
    private ApartmentInfoService apartmentInfoService;

    /**
     * 根据id获取公寓详情（适配小程序展示）
     */
    @Operation(summary = "获取公寓详情", description = "根据id获取公寓详细信息，返回字段适配小程序端展示")
    @BehaviorLog(value = BehaviorType.BROWSE, desc = "小程序浏览公寓详情", targetType = "apartment", targetId = "#id")
    @GetMapping("/getDetailById")
    public Result<MiniApartmentDetailVo> getDetailById(
            @Parameter(description = "公寓ID") @RequestParam Long id) {

        ApartmentDetailVo detailVo = apartmentInfoService.getDetailById(id);
        if (detailVo == null) {
            return Result.ok();
        }

        MiniApartmentDetailVo miniVo = new MiniApartmentDetailVo();
        miniVo.setId(detailVo.getId());
        miniVo.setName(detailVo.getName());
        miniVo.setIntroduction(detailVo.getIntroduction());
        miniVo.setProvinceName(detailVo.getProvinceName());
        miniVo.setCityName(detailVo.getCityName());
        miniVo.setDistrictName(detailVo.getDistrictName());
        miniVo.setAddressDetail(detailVo.getAddressDetail());
        miniVo.setLatitude(detailVo.getLatitude());
        miniVo.setLongitude(detailVo.getLongitude());
        miniVo.setPhone(detailVo.getPhone());
        miniVo.setMinRent(detailVo.getMinRent());
        miniVo.setMaxRent(detailVo.getMaxRent());

        // 图片URL列表裁剪
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

        return Result.ok(miniVo);
    }
}
