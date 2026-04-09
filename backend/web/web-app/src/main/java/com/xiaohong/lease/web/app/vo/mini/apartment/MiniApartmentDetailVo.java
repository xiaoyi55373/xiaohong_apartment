package com.xiaohong.lease.web.app.vo.mini.apartment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 小程序端公寓详情VO
 * 对APP端公寓详情做字段裁剪，适配小程序展示
 *
 * @author 小红
 * @createDate 2026-04-07
 */
@Data
@Schema(description = "小程序端公寓详情")
public class MiniApartmentDetailVo {

    @Schema(description = "公寓id")
    private Long id;

    @Schema(description = "公寓名称")
    private String name;

    @Schema(description = "公寓简介")
    private String introduction;

    @Schema(description = "省份名称")
    private String provinceName;

    @Schema(description = "城市名称")
    private String cityName;

    @Schema(description = "区域名称")
    private String districtName;

    @Schema(description = "详细地址")
    private String addressDetail;

    @Schema(description = "纬度")
    private String latitude;

    @Schema(description = "经度")
    private String longitude;

    @Schema(description = "公寓电话")
    private String phone;

    @Schema(description = "图片列表")
    private List<String> imageList;

    @Schema(description = "标签名称列表")
    private List<String> labelNames;

    @Schema(description = "配套名称列表")
    private List<String> facilityNames;

    @Schema(description = "最小租金")
    private BigDecimal minRent;

    @Schema(description = "最大租金")
    private BigDecimal maxRent;
}
