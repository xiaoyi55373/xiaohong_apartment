package com.xiaohong.lease.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 公寓评分记录表
 *
 * @author 小红
 * @createDate 2026-04-03
 */
@Schema(description = "公寓评分记录表")
@TableName(value = "apartment_rating")
@Data
@EqualsAndHashCode(callSuper = true)
public class ApartmentRating extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    @TableField(value = "user_id")
    private Long userId;

    @Schema(description = "公寓ID")
    @TableField(value = "apartment_id")
    private Long apartmentId;

    @Schema(description = "环境评分(1-5)")
    @TableField(value = "environment_score")
    private BigDecimal environmentScore;

    @Schema(description = "交通评分(1-5)")
    @TableField(value = "traffic_score")
    private BigDecimal trafficScore;

    @Schema(description = "设施评分(1-5)")
    @TableField(value = "facility_score")
    private BigDecimal facilityScore;

    @Schema(description = "服务评分(1-5)")
    @TableField(value = "service_score")
    private BigDecimal serviceScore;

    @Schema(description = "性价比评分(1-5)")
    @TableField(value = "value_score")
    private BigDecimal valueScore;

    @Schema(description = "综合评分(1-5)")
    @TableField(value = "overall_score")
    private BigDecimal overallScore;

    @Schema(description = "评分内容")
    @TableField(value = "content")
    private String content;

    @Schema(description = "是否匿名(0-否 1-是)")
    @TableField(value = "is_anonymous")
    private Integer isAnonymous;

    @Schema(description = "状态(0-待审核 1-已通过 2-已拒绝)")
    @TableField(value = "status")
    private Integer status;
}
