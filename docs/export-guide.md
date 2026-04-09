# 小红公寓claw 数据导出功能指南

## 概述

数据导出功能支持将系统中的公寓信息、租约信息、预约信息和系统用户信息导出为Excel或PDF格式，方便数据分析和报表生成。

## 功能特性

### 支持的导出格式

1. **Excel (.xlsx)** - 使用EasyExcel实现，支持大数据量导出
2. **PDF (.pdf)** - 使用OpenPDF实现，支持中文显示和品牌样式

### 支持的数据类型

| 数据类型 | 导出字段 | 筛选条件 |
|---------|---------|---------|
| 公寓信息 | ID、名称、省份、城市、区县、地址、电话、发布状态、评分、创建时间 | 省份、城市、区县 |
| 租约信息 | ID、承租人、手机号、身份证、公寓、房间、租期、租金、押金、状态 | 公寓、房间、姓名、手机 |
| 预约信息 | ID、用户ID、预约人、手机号、公寓、预约时间、状态 | 公寓、姓名、手机 |
| 系统用户 | ID、用户名、姓名、类型、手机、岗位、状态 | 用户名、姓名、手机 |

## API接口

### 1. 导出公寓信息

```
GET /admin/export/apartment?exportType={type}&provinceId={id}&cityId={id}&districtId={id}
```

**参数说明：**
- `exportType` (必填): 导出类型，`excel` 或 `pdf`
- `provinceId` (可选): 省份ID
- `cityId` (可选): 城市ID
- `districtId` (可选): 区县ID

**示例：**
```bash
curl -X GET "http://localhost:8080/admin/export/apartment?exportType=excel&provinceId=1" \
  -H "Authorization: Bearer {token}" \
  --output apartments.xlsx
```

### 2. 导出租约信息

```
GET /admin/export/agreement?exportType={type}&apartmentId={id}&roomId={id}&name={name}&phone={phone}
```

**参数说明：**
- `exportType` (必填): 导出类型
- `apartmentId` (可选): 公寓ID
- `roomId` (可选): 房间ID
- `name` (可选): 承租人姓名
- `phone` (可选): 手机号码

### 3. 导出预约信息

```
GET /admin/export/appointment?exportType={type}&apartmentId={id}&name={name}&phone={phone}
```

**参数说明：**
- `exportType` (必填): 导出类型
- `apartmentId` (可选): 公寓ID
- `name` (可选): 预约人姓名
- `phone` (可选): 手机号码

### 4. 导出系统用户信息

```
GET /admin/export/systemUser?exportType={type}&username={username}&name={name}&phone={phone}
```

**参数说明：**
- `exportType` (必填): 导出类型
- `username` (可选): 用户名
- `name` (可选): 姓名
- `phone` (可选): 手机号码

## 技术实现

### 核心组件

```
com.xiaohong.lease.common.export
├── ExportType.java          # 导出类型枚举
├── ExcelExportService.java  # Excel导出服务
├── PdfExportService.java    # PDF导出服务
└── ExportUtil.java          # 导出工具类（大数据量分批导出）
```

### 导出VO对象

```
com.xiaohong.lease.web.admin.vo.export
├── ApartmentExportVo.java   # 公寓导出VO
├── AgreementExportVo.java   # 租约导出VO
├── AppointmentExportVo.java # 预约导出VO
└── SystemUserExportVo.java  # 用户导出VO
```

### 控制器

```
com.xiaohong.lease.web.admin.controller.export
└── ExportController.java    # 导出控制器
```

## 使用示例

### 在Controller中添加导出功能

```java
@CheckPermission
@OperationLog(title = "数据导出", businessType = BusinessType.EXPORT)
@Operation(summary = "导出数据")
@GetMapping("/export")
public void exportData(@RequestParam String exportType, HttpServletResponse response) {
    // 1. 查询数据
    List<Data> dataList = dataService.list();
    
    // 2. 转换为导出VO
    List<ExportVo> exportList = convertToExportVo(dataList);
    
    // 3. 执行导出
    String fileName = "数据_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    
    if (ExportType.PDF.name().equalsIgnoreCase(exportType)) {
        // PDF导出
        String[] headers = {"字段1", "字段2", "字段3"};
        List<String[]> data = convertToStringArray(exportList);
        pdfExportService.export(response, fileName, "报表标题", headers, data);
    } else {
        // Excel导出
        excelExportService.export(response, fileName, "Sheet名称", exportList, ExportVo.class);
    }
}
```

### 大数据量导出

对于超过10000条的数据，建议使用分批导出：

```java
// 使用ExportUtil进行大数据量导出
ExportUtil.exportLargeData(
    response,
    "大数据导出",
    "数据Sheet",
    ExportVo.class,
    totalCount,
    1000, // 每批1000条
    pageParam -> {
        // 分批查询数据
        return dataService.listPage(pageParam.getOffset(), pageParam.getLimit());
    }
);
```

### 带进度回调的导出

```java
ExportUtil.exportLargeDataWithProgress(
    response,
    "进度导出",
    "数据Sheet",
    ExportVo.class,
    totalCount,
    1000,
    pageParam -> dataService.listPage(pageParam.getOffset(), pageParam.getLimit()),
    (current, total) -> {
        // 进度回调，可用于WebSocket推送进度
        log.info("导出进度: {}/{} ({:.1f}%)", current, total, (double) current / total * 100);
    }
);
```

## 配置说明

### Maven依赖

```xml
<!-- EasyExcel -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>easyexcel</artifactId>
    <version>3.3.2</version>
</dependency>

<!-- OpenPDF -->
<dependency>
    <groupId>com.github.librepdf</groupId>
    <artifactId>openpdf</artifactId>
    <version>1.3.30</version>
</dependency>
```

### 导出VO注解

```java
@Data
@HeadRowHeight(25)  // 表头行高
public class ExportVo {
    
    @ExcelProperty(value = "字段名称", index = 0)  // 列标题和顺序
    @ColumnWidth(20)  // 列宽
    private String fieldName;
    
    @ExcelProperty(value = "数值", index = 1)
    @ColumnWidth(15)
    private Integer value;
}
```

## PDF样式说明

PDF导出采用小红公寓品牌样式：

- **品牌色**：珊瑚红 (#FF6B6B)
- **表头背景**：珊瑚红
- **表头文字**：白色
- **斑马纹**：淡粉色 (#FFF5F5)
- **页脚**：包含品牌信息和导出时间

## 性能优化

1. **分批处理**：大数据量使用分批查询和写入，避免内存溢出
2. **流式写入**：Excel使用流式写入，PDF使用增量写入
3. **异步导出**：超大数据量建议使用异步任务，通过邮件发送下载链接
4. **缓存优化**：频繁导出的数据可添加缓存

## 安全考虑

1. **权限控制**：所有导出接口需要`@CheckPermission`注解
2. **操作日志**：使用`@OperationLog`记录导出操作
3. **数据脱敏**：敏感字段（如手机号、身份证）可添加脱敏处理
4. **导出限制**：建议限制单次导出数据量（默认10000条）

## 常见问题

### 1. 中文乱码

PDF导出使用系统字体（STSong-Light），如不可用则使用默认字体。确保服务器已安装中文字体。

### 2. 内存溢出

大数据量导出时请使用`ExportUtil.exportLargeData`方法，或增加JVM内存。

### 3. 导出超时

大数据量导出可能导致HTTP超时，建议：
- 使用异步导出
- 增加Nginx/Apache超时时间
- 使用WebSocket推送进度

## 测试

运行单元测试：

```bash
cd /Users/dream3.14/Desktop/90/1208158461/公寓项目/代码/代码/后端/lease
mvn test -Dtest=ExcelExportServiceTest,PdfExportServiceTest,ExportTypeTest
```

## 更新日志

### 2026-04-03
- 初始版本发布
- 支持Excel和PDF导出
- 支持公寓、租约、预约、用户数据导出
- 支持大数据量分批导出
