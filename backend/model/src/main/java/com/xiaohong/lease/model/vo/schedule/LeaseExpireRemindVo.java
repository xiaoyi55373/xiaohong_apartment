package com.xiaohong.lease.model.vo.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 租约到期提醒VO
 *
 * @author 小红
 */
@Data
@Schema(description = "租约到期提醒信息")
public class LeaseExpireRemindVo {

    @Schema(description = "租约ID")
    private Long leaseId;

    @Schema(description = "承租人姓名")
    private String name;

    @Schema(description = "承租人手机号")
    private String phone;

    @Schema(description = "承租人邮箱")
    private String email;

    @Schema(description = "公寓ID")
    private Long apartmentId;

    @Schema(description = "公寓名称")
    private String apartmentName;

    @Schema(description = "公寓地址")
    private String apartmentAddress;

    @Schema(description = "房间ID")
    private Long roomId;

    @Schema(description = "房间号")
    private String roomNumber;

    @Schema(description = "租约开始日期")
    private Date leaseStartDate;

    @Schema(description = "租约结束日期")
    private Date leaseEndDate;

    @Schema(description = "剩余天数")
    private Integer daysRemaining;

    @Schema(description = "租金")
    private BigDecimal rent;

    @Schema(description = "押金")
    private BigDecimal deposit;
}
