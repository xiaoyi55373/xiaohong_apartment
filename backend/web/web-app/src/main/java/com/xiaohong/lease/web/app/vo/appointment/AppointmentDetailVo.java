package com.xiaohong.lease.web.app.vo.appointment;

import com.xiaohong.lease.model.entity.ViewAppointment;
import com.xiaohong.lease.web.app.vo.apartment.ApartmentItemVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "APP端预约看房详情")
public class AppointmentDetailVo extends ViewAppointment {

    @Schema(description = "公寓基本信息")
    private ApartmentItemVo apartmentItemVo;

    // 显式添加 setter 方法
    public void setApartmentItemVo(ApartmentItemVo apartmentItemVo) {
        this.apartmentItemVo = apartmentItemVo;
    }
}
