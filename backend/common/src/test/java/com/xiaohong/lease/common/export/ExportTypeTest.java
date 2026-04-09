package com.xiaohong.lease.common.export;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 导出类型枚举测试类
 *
 * @author 小红
 * @date 2026-04-03
 */
class ExportTypeTest {

    @Test
    void testEnumValues() {
        // 验证枚举值
        ExportType[] types = ExportType.values();
        assertEquals(2, types.length);
        assertEquals(ExportType.EXCEL, types[0]);
        assertEquals(ExportType.PDF, types[1]);
    }

    @Test
    void testExcelProperties() {
        // 验证Excel类型属性
        assertEquals("excel", ExportType.EXCEL.getExtension());
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", 
                     ExportType.EXCEL.getContentType());
    }

    @Test
    void testPdfProperties() {
        // 验证PDF类型属性
        assertEquals("pdf", ExportType.PDF.getExtension());
        assertEquals("application/pdf", ExportType.PDF.getContentType());
    }

    @Test
    void testFromStringExcel() {
        // 测试从字符串解析Excel类型
        assertEquals(ExportType.EXCEL, ExportType.fromString("EXCEL"));
        assertEquals(ExportType.EXCEL, ExportType.fromString("excel"));
        assertEquals(ExportType.EXCEL, ExportType.fromString("Excel"));
    }

    @Test
    void testFromStringPdf() {
        // 测试从字符串解析PDF类型
        assertEquals(ExportType.PDF, ExportType.fromString("PDF"));
        assertEquals(ExportType.PDF, ExportType.fromString("pdf"));
        assertEquals(ExportType.PDF, ExportType.fromString("Pdf"));
    }

    @Test
    void testFromStringInvalid() {
        // 测试无效字符串应返回默认值EXCEL
        assertEquals(ExportType.EXCEL, ExportType.fromString("invalid"));
        assertEquals(ExportType.EXCEL, ExportType.fromString(""));
        assertEquals(ExportType.EXCEL, ExportType.fromString(null));
    }

    @Test
    void testFromStringWithSpaces() {
        // 测试带空格的字符串（应该返回默认值EXCEL）
        assertEquals(ExportType.EXCEL, ExportType.fromString(" excel "));
        assertEquals(ExportType.EXCEL, ExportType.fromString(" pdf "));
    }
}
