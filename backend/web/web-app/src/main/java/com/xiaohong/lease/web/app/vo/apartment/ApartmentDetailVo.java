package com.xiaohong.lease.web.app.vo.apartment;

import com.xiaohong.lease.model.entity.FacilityInfo;
import com.xiaohong.lease.model.entity.LabelInfo;
import com.xiaohong.lease.web.app.vo.graph.GraphVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "APP端公寓详情")
public class ApartmentDetailVo {

    @Schema(description = "公寓id")
    private Long id;

    @Schema(description = "公寓名称")
    private String name;

    @Schema(description = "公寓简介")
    private String introduction;

    @Schema(description = "省份id")
    private Long provinceId;

    @Schema(description = "省份名称")
    private String provinceName;

    @Schema(description = "城市id")
    private Long cityId;

    @Schema(description = "城市名称")
    private String cityName;

    @Schema(description = "区域id")
    private Long districtId;

    @Schema(description = "区域名称")
    private String districtName;

    @Schema(description = "详细地址")
    private String addressDetail;

    @Schema(description = "纬度")
    private String latitude;

    @Schema(description = "经度")
    private String longitude;

    @Schema(description = "公寓电话")
    private String phone;

    @Schema(description = "是否发布")
    private Boolean isRelease;

    @Schema(description = "图片列表")
    private List<GraphVo> graphVoList;

    @Schema(description = "标签列表")
    private List<LabelInfo> labelInfoList;

    @Schema(description = "配套列表")
    private List<FacilityInfo> facilityInfoList;

    @Schema(description = "最小租金")
    private BigDecimal minRent;

    @Schema(description = "最大租金")
    private BigDecimal maxRent;

    @Schema(description = "是否已删除")
    private Boolean isDelete;

    // 显式添加 setter 方法
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setIntroduction(String introduction) { this.introduction = introduction; }
    public void setProvinceId(Long provinceId) { this.provinceId = provinceId; }
    public void setProvinceName(String provinceName) { this.provinceName = provinceName; }
    public void setCityId(Long cityId) { this.cityId = cityId; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    public void setDistrictId(Long districtId) { this.districtId = districtId; }
    public void setDistrictName(String districtName) { this.districtName = districtName; }
    public void setAddressDetail(String addressDetail) { this.addressDetail = addressDetail; }
    public void setLatitude(String latitude) { this.latitude = latitude; }
    public void setLongitude(String longitude) { this.longitude = longitude; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setIsRelease(Boolean isRelease) { this.isRelease = isRelease; }
    public void setGraphVoList(List<GraphVo> graphVoList) { this.graphVoList = graphVoList; }
    public void setLabelInfoList(List<LabelInfo> labelInfoList) { this.labelInfoList = labelInfoList; }
    public void setFacilityInfoList(List<FacilityInfo> facilityInfoList) { this.facilityInfoList = facilityInfoList; }
    public void setMinRent(BigDecimal minRent) { this.minRent = minRent; }
    public void setMaxRent(BigDecimal maxRent) { this.maxRent = maxRent; }
    public void setIsDelete(Boolean isDelete) { this.isDelete = isDelete; }
}
