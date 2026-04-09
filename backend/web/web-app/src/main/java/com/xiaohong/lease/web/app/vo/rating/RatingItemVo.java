package com.xiaohong.lease.web.app.vo.rating;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 评分列表项VO
 *
 * @author 小红
 * @createDate 2026-04-03
 */
@Data
@Schema(description = "评分列表项")
public class RatingItemVo {

    @Schema(description = "评分ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "公寓ID")
    private Long apartmentId;

    @Schema(description = "综合评分")
    private BigDecimal overallScore;

    @Schema(description = "环境评分")
    private BigDecimal environmentScore;

    @Schema(description = "交通评分")
    private BigDecimal trafficScore;

    @Schema(description = "设施评分")
    private BigDecimal facilityScore;

    @Schema(description = "服务评分")
    private BigDecimal serviceScore;

    @Schema(description = "性价比评分")
    private BigDecimal valueScore;

    @Schema(description = "评分内容")
    private String content;

    @Schema(description = "是否匿名")
    private Integer anonymous;

    @Schema(description = "创建时间")
    private Date createTime;
}
