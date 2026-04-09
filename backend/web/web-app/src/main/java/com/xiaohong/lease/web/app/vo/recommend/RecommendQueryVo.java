package com.xiaohong.lease.web.app.vo.recommend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 推荐查询VO
 * @author 小红
 * @createDate 2026-04-03
 */
@Schema(description = "推荐查询VO")
@Data
public class RecommendQueryVo {

    @Schema(description = "推荐数量，默认10")
    private Integer limit = 10;

    @Schema(description = "是否包含已浏览过的房源，默认false")
    private Boolean includeViewed = false;

    @Schema(description = "推荐类型：ALL-全部, CONTENT-基于内容, COLLABORATIVE-协同过滤, HOT-热门")
    private String type = "ALL";
}
