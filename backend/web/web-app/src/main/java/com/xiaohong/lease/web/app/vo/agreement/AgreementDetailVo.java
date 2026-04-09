package com.xiaohong.lease.web.app.vo.agreement;

import com.xiaohong.lease.model.entity.LeaseAgreement;
import com.xiaohong.lease.web.app.vo.graph.GraphVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "租约详细信息")
public class AgreementDetailVo extends LeaseAgreement {

    @Schema(description = "公寓名称")
    private String apartmentName;

    @Schema(description = "公寓图片列表")
    private List<GraphVo> apartmentGraphVoList;

    @Schema(description = "房间号")
    private String roomNumber;

    @Schema(description = "房间图片列表")
    private List<GraphVo> roomGraphVoList;

    @Schema(description = "支付方式")
    private String paymentTypeName;

    @Schema(description = "租期月数")
    private Integer leaseTermMonthCount;

    @Schema(description = "租期单位")
    private String leaseTermUnit;

    // 显式添加 setter 方法
    public void setApartmentName(String apartmentName) {
        this.apartmentName = apartmentName;
    }

    public void setApartmentGraphVoList(List<GraphVo> apartmentGraphVoList) {
        this.apartmentGraphVoList = apartmentGraphVoList;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setRoomGraphVoList(List<GraphVo> roomGraphVoList) {
        this.roomGraphVoList = roomGraphVoList;
    }

    public void setPaymentTypeName(String paymentTypeName) {
        this.paymentTypeName = paymentTypeName;
    }

    public void setLeaseTermMonthCount(Integer leaseTermMonthCount) {
        this.leaseTermMonthCount = leaseTermMonthCount;
    }

    public void setLeaseTermUnit(String leaseTermUnit) {
        this.leaseTermUnit = leaseTermUnit;
    }
}
