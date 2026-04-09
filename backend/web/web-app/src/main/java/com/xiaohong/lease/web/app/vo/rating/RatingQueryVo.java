package com.xiaohong.lease.web.app.vo.rating;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 评分查询VO
 *
 * @author 小红
 * @createDate 2026-04-03
 */
@Data
@Schema(description = "评分查询条件")
public class RatingQueryVo {

    @Schema(description = "公寓ID", required = true)
    private Long apartmentId;

    @Schema(description = "评分筛选：1-最新 2-好评 3-中评 4-差评 5-有图")
    private Integer filterType;

    @Schema(description = "排序方式：newest-最新 score-评分")
    private String sortType;
}
