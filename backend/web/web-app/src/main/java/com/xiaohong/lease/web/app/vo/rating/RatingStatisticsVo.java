package com.xiaohong.lease.web.app.vo.rating;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 评分统计VO
 *
 * @author 小红
 * @createDate 2026-04-03
 */
@Data
@Schema(description = "评分统计数据")
public class RatingStatisticsVo {

    @Schema(description = "公寓ID")
    private Long apartmentId;

    @Schema(description = "平均综合评分")
    private BigDecimal avgOverallScore;

    @Schema(description = "平均环境评分")
    private BigDecimal avgEnvironmentScore;

    @Schema(description = "平均交通评分")
    private BigDecimal avgTrafficScore;

    @Schema(description = "平均设施评分")
    private BigDecimal avgFacilityScore;

    @Schema(description = "平均服务评分")
    private BigDecimal avgServiceScore;

    @Schema(description = "平均性价比评分")
    private BigDecimal avgValueScore;

    @Schema(description = "评分总人数")
    private Integer totalCount;

    @Schema(description = "各星级评分人数分布")
    private Map<String, Integer> scoreDistribution;

    @Schema(description = "各维度评分占比")
    private Map<String, BigDecimal> dimensionScores;

    @Schema(description = "当前用户是否已评分")
    private Boolean hasRated;

    @Schema(description = "当前用户评分ID")
    private Long userRatingId;
}
