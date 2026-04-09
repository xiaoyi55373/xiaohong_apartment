package com.xiaohong.lease.web.admin.vo.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.util.Date;

/**
 * 系统用户导出VO
 *
 * @author 小红
 * @date 2026-04-03
 */
@Data
@HeadRowHeight(25)
public class SystemUserExportVo {

    @ExcelProperty(value = "用户ID", index = 0)
    @ColumnWidth(10)
    private Long id;

    @ExcelProperty(value = "用户名", index = 1)
    @ColumnWidth(15)
    private String username;

    @ExcelProperty(value = "姓名", index = 2)
    @ColumnWidth(12)
    private String name;

    @ExcelProperty(value = "用户类型", index = 3)
    @ColumnWidth(12)
    private String type;

    @ExcelProperty(value = "手机号码", index = 4)
    @ColumnWidth(15)
    private String phone;

    @ExcelProperty(value = "岗位", index = 5)
    @ColumnWidth(15)
    private String postName;

    @ExcelProperty(value = "账号状态", index = 6)
    @ColumnWidth(12)
    private String status;

    @ExcelProperty(value = "创建时间", index = 7)
    @ColumnWidth(20)
    private Date createTime;

    @ExcelProperty(value = "备注", index = 8)
    @ColumnWidth(30)
    private String additionalInfo;
}
