package com.xiaohong.lease.web.app.vo.rating;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 评分提交VO
 *
 * @author 小红
 * @createDate 2026-04-03
 */
@Data
@Schema(description = "评分提交请求")
public class RatingSubmitVo {

    @Schema(description = "公寓ID", required = true)
    private Long apartmentId;

    @Schema(description = "环境评分(1-5)", required = true)
    private BigDecimal environmentScore;

    @Schema(description = "交通评分(1-5)", required = true)
    private BigDecimal trafficScore;

    @Schema(description = "设施评分(1-5)", required = true)
    private BigDecimal facilityScore;

    @Schema(description = "服务评分(1-5)", required = true)
    private BigDecimal serviceScore;

    @Schema(description = "性价比评分(1-5)", required = true)
    private BigDecimal valueScore;

    @Schema(description = "评分内容/评论")
    private String content;

    @Schema(description = "是否匿名(0-否 1-是)，默认0")
    private Integer isAnonymous;
}
