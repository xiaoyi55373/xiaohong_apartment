package com.xiaohong.lease.web.app.vo.recommend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户偏好VO
 * @author 小红
 * @createDate 2026-04-03
 */
@Schema(description = "用户偏好VO")
@Data
public class UserPreferenceVo {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "偏好区域ID列表")
    private List<Long> preferredDistrictIds;

    @Schema(description = "偏好城市ID列表")
    private List<Long> preferredCityIds;

    @Schema(description = "偏好标签列表")
    private List<String> preferredLabels;

    @Schema(description = "偏好价格范围-最低")
    private BigDecimal minRent;

    @Schema(description = "偏好价格范围-最高")
    private BigDecimal maxRent;

    @Schema(description = "平均租金")
    private BigDecimal avgRent;

    @Schema(description = "浏览房间数量")
    private Integer browseCount;

    @Schema(description = "标签权重Map")
    private Map<String, Double> labelWeights;

    @Schema(description = "区域权重Map")
    private Map<Long, Double> districtWeights;
}
