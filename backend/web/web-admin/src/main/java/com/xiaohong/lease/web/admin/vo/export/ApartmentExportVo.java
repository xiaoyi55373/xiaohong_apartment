package com.xiaohong.lease.web.admin.vo.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 公寓信息导出VO
 *
 * @author 小红
 * @date 2026-04-03
 */
@Data
@HeadRowHeight(25)
public class ApartmentExportVo {

    @ExcelProperty(value = "公寓ID", index = 0)
    @ColumnWidth(10)
    private Long id;

    @ExcelProperty(value = "公寓名称", index = 1)
    @ColumnWidth(25)
    private String name;

    @ExcelProperty(value = "省份", index = 2)
    @ColumnWidth(12)
    private String provinceName;

    @ExcelProperty(value = "城市", index = 3)
    @ColumnWidth(12)
    private String cityName;

    @ExcelProperty(value = "区县", index = 4)
    @ColumnWidth(12)
    private String districtName;

    @ExcelProperty(value = "详细地址", index = 5)
    @ColumnWidth(35)
    private String addressDetail;

    @ExcelProperty(value = "经度", index = 6)
    @ColumnWidth(12)
    private String latitude;

    @ExcelProperty(value = "纬度", index = 7)
    @ColumnWidth(12)
    private String longitude;

    @ExcelProperty(value = "联系电话", index = 8)
    @ColumnWidth(15)
    private String phone;

    @ExcelProperty(value = "发布状态", index = 9)
    @ColumnWidth(12)
    private String releaseStatus;

    @ExcelProperty(value = "平均评分", index = 10)
    @ColumnWidth(12)
    private BigDecimal avgScore;

    @ExcelProperty(value = "评分人数", index = 11)
    @ColumnWidth(12)
    private Integer scoreCount;

    @ExcelProperty(value = "创建时间", index = 12)
    @ColumnWidth(20)
    private Date createTime;

    @ExcelProperty(value = "公寓介绍", index = 13)
    @ColumnWidth(40)
    private String introduction;
}
