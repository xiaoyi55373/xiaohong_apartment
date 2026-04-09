package com.xiaohong.lease.web.admin.vo.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.util.Date;

/**
 * 预约信息导出VO
 *
 * @author 小红
 * @date 2026-04-03
 */
@Data
@HeadRowHeight(25)
public class AppointmentExportVo {

    @ExcelProperty(value = "预约ID", index = 0)
    @ColumnWidth(10)
    private Long id;

    @ExcelProperty(value = "用户ID", index = 1)
    @ColumnWidth(10)
    private Long userId;

    @ExcelProperty(value = "预约人姓名", index = 2)
    @ColumnWidth(12)
    private String name;

    @ExcelProperty(value = "手机号码", index = 3)
    @ColumnWidth(15)
    private String phone;

    @ExcelProperty(value = "公寓名称", index = 4)
    @ColumnWidth(25)
    private String apartmentName;

    @ExcelProperty(value = "预约时间", index = 5)
    @ColumnWidth(20)
    private Date appointmentTime;

    @ExcelProperty(value = "预约状态", index = 6)
    @ColumnWidth(12)
    private String appointmentStatus;

    @ExcelProperty(value = "创建时间", index = 7)
    @ColumnWidth(20)
    private Date createTime;

    @ExcelProperty(value = "备注", index = 8)
    @ColumnWidth(30)
    private String additionalInfo;
}
