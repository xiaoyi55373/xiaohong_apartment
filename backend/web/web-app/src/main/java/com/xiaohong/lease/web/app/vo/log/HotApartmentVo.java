package com.xiaohong.lease.web.app.vo.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 热门房源VO
 *
 * @author 小红
 */
@Data
@Schema(description = "热门房源排行")
public class HotApartmentVo {

    @Schema(description = "公寓ID")
    private Long apartmentId;

    @Schema(description = "浏览次数")
    private Long browseCount;

    @Schema(description = "独立访客数")
    private Long uniqueUserCount;
}
