package com.xiaohong.lease.web.app.vo.mini.room;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 小程序端房间列表项VO
 * 对APP端房间列表做字段裁剪，适配小程序展示
 *
 * @author 小红
 * @createDate 2026-04-07
 */
@Data
@Schema(description = "小程序端房间列表项")
public class MiniRoomItemVo {

    @Schema(description = "房间id")
    private Long id;

    @Schema(description = "房间号")
    private String roomNumber;

    @Schema(description = "租金（元/月）")
    private BigDecimal rent;

    @Schema(description = "首图")
    private String firstImage;

    @Schema(description = "标签名称列表")
    private List<String> labelNames;

    @Schema(description = "公寓id")
    private Long apartmentId;

    @Schema(description = "公寓名称")
    private String apartmentName;

    @Schema(description = "区域名称")
    private String districtName;

    @Schema(description = "城市名称")
    private String cityName;
}
