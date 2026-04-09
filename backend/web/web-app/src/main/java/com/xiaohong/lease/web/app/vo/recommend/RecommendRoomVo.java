package com.xiaohong.lease.web.app.vo.recommend;

import com.xiaohong.lease.model.entity.ApartmentInfo;
import com.xiaohong.lease.model.entity.LabelInfo;
import com.xiaohong.lease.web.app.vo.graph.GraphVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 推荐房间VO
 * @author 小红
 * @createDate 2026-04-03
 */
@Schema(description = "推荐房间VO")
@Data
public class RecommendRoomVo {

    @Schema(description = "房间id")
    private Long id;

    @Schema(description = "房间号")
    private String roomNumber;

    @Schema(description = "租金（元/月）")
    private BigDecimal rent;

    @Schema(description = "房间图片列表")
    private List<GraphVo> graphVoList;

    @Schema(description = "房间标签列表")
    private List<LabelInfo> labelInfoList;

    @Schema(description = "房间所属公寓信息")
    private ApartmentInfo apartmentInfo;

    @Schema(description = "推荐得分")
    private Double recommendScore;

    @Schema(description = "推荐原因")
    private String recommendReason;

    @Schema(description = "推荐类型：CONTENT-基于内容, COLLABORATIVE-协同过滤, HOT-热门推荐")
    private String recommendType;
}
