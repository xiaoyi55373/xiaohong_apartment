package com.xiaohong.lease.web.app.vo.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 行为统计VO
 *
 * @author 小红
 */
@Data
@Schema(description = "行为统计数据")
public class BehaviorStatisticsVo {

    @Schema(description = "总PV数")
    private Long totalPv;

    @Schema(description = "总UV数")
    private Long totalUv;

    @Schema(description = "今日PV数")
    private Long todayPv;

    @Schema(description = "今日UV数")
    private Long todayUv;

    @Schema(description = "行为分布")
    private List<BehaviorTrendVo> distribution;

    @Schema(description = "热门房源排行")
    private List<HotApartmentVo> hotApartments;

    @Schema(description = "行为趋势")
    private List<BehaviorTrendVo> trend;
}
