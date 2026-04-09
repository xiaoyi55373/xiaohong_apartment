package com.xiaohong.lease.web.app.vo.mini.room;

import com.xiaohong.lease.model.entity.LeaseTerm;
import com.xiaohong.lease.model.entity.PaymentType;
import com.xiaohong.lease.web.app.vo.attr.AttrValueVo;
import com.xiaohong.lease.web.app.vo.fee.FeeValueVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 小程序端房间详情VO
 * 对APP端房间详情做字段裁剪，适配小程序展示
 *
 * @author 小红
 * @createDate 2026-04-07
 */
@Data
@Schema(description = "小程序端房间详情")
public class MiniRoomDetailVo {

    @Schema(description = "房间id")
    private Long id;

    @Schema(description = "房间号")
    private String roomNumber;

    @Schema(description = "租金（元/月）")
    private BigDecimal rent;

    @Schema(description = "公寓id")
    private Long apartmentId;

    @Schema(description = "公寓名称")
    private String apartmentName;

    @Schema(description = "详细地址")
    private String addressDetail;

    @Schema(description = "纬度")
    private String latitude;

    @Schema(description = "经度")
    private String longitude;

    @Schema(description = "公寓电话")
    private String phone;

    @Schema(description = "图片列表")
    private List<String> imageList;

    @Schema(description = "属性信息列表")
    private List<AttrValueVo> attrValueVoList;

    @Schema(description = "配套名称列表")
    private List<String> facilityNames;

    @Schema(description = "标签名称列表")
    private List<String> labelNames;

    @Schema(description = "支付方式列表")
    private List<PaymentType> paymentTypeList;

    @Schema(description = "杂费列表")
    private List<FeeValueVo> feeValueVoList;

    @Schema(description = "租期列表")
    private List<LeaseTerm> leaseTermList;

    @Schema(description = "是否已入住")
    private Boolean isCheckIn;
}
