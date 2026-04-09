package com.xiaohong.lease.web.admin.vo.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 租约信息导出VO
 *
 * @author 小红
 * @date 2026-04-03
 */
@Data
@HeadRowHeight(25)
public class AgreementExportVo {

    @ExcelProperty(value = "租约ID", index = 0)
    @ColumnWidth(10)
    private Long id;

    @ExcelProperty(value = "承租人姓名", index = 1)
    @ColumnWidth(12)
    private String name;

    @ExcelProperty(value = "手机号码", index = 2)
    @ColumnWidth(15)
    private String phone;

    @ExcelProperty(value = "身份证号", index = 3)
    @ColumnWidth(20)
    private String identificationNumber;

    @ExcelProperty(value = "公寓名称", index = 4)
    @ColumnWidth(25)
    private String apartmentName;

    @ExcelProperty(value = "房间号", index = 5)
    @ColumnWidth(12)
    private String roomNumber;

    @ExcelProperty(value = "租约开始日期", index = 6)
    @ColumnWidth(15)
    private Date leaseStartDate;

    @ExcelProperty(value = "租约结束日期", index = 7)
    @ColumnWidth(15)
    private Date leaseEndDate;

    @ExcelProperty(value = "租期", index = 8)
    @ColumnWidth(12)
    private String leaseTermName;

    @ExcelProperty(value = "租金(元/月)", index = 9)
    @ColumnWidth(14)
    private BigDecimal rent;

    @ExcelProperty(value = "押金(元)", index = 10)
    @ColumnWidth(12)
    private BigDecimal deposit;

    @ExcelProperty(value = "支付方式", index = 11)
    @ColumnWidth(12)
    private String paymentTypeName;

    @ExcelProperty(value = "租约状态", index = 12)
    @ColumnWidth(12)
    private String status;

    @ExcelProperty(value = "租约来源", index = 13)
    @ColumnWidth(12)
    private String sourceType;

    @ExcelProperty(value = "创建时间", index = 14)
    @ColumnWidth(20)
    private Date createTime;

    @ExcelProperty(value = "备注", index = 15)
    @ColumnWidth(30)
    private String additionalInfo;
}
