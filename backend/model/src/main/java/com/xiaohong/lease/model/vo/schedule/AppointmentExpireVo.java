package com.xiaohong.lease.model.vo.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 预约过期处理VO
 *
 * @author 小红
 */
@Data
@Schema(description = "预约过期处理信息")
public class AppointmentExpireVo {

    @Schema(description = "预约ID")
    private Long appointmentId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户姓名")
    private String name;

    @Schema(description = "用户手机号")
    private String phone;

    @Schema(description = "公寓ID")
    private Long apartmentId;

    @Schema(description = "公寓名称")
    private String apartmentName;

    @Schema(description = "预约时间")
    private Date appointmentTime;

    @Schema(description = "预约状态")
    private String appointmentStatus;

    @Schema(description = "是否已过期")
    private Boolean isExpired;
}
