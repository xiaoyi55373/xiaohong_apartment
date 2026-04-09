package com.xiaohong.lease.web.app.vo.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 行为趋势VO
 *
 * @author 小红
 */
@Data
@Schema(description = "行为趋势数据")
public class BehaviorTrendVo {

    @Schema(description = "日期标签或行为类型")
    private String dateLabel;

    @Schema(description = "数量")
    private Long count;
}
