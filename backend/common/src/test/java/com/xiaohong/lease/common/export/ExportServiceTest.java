package com.xiaohong.lease.common.export;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 导出服务测试类
 *
 * @author 小红
 * @date 2026-04-03
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ExportTestConfig.class)
public class ExportServiceTest {

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private PdfExportService pdfExportService;

    @Test
    public void testExportTypeEnum() {
        // 测试导出类型枚举
        assertEquals("excel", ExportType.EXCEL.getExtension());
        assertEquals("pdf", ExportType.PDF.getExtension());
        assertNotNull(ExportType.EXCEL.getContentType());
        assertNotNull(ExportType.PDF.getContentType());

        // 测试fromString方法
        assertEquals(ExportType.EXCEL, ExportType.fromString("excel"));
        assertEquals(ExportType.PDF, ExportType.fromString("pdf"));
        assertEquals(ExportType.EXCEL, ExportType.fromString("EXCEL"));
        assertEquals(ExportType.PDF, ExportType.fromString("PDF"));
        assertEquals(ExportType.EXCEL, ExportType.fromString("unknown"));
    }

    @Test
    public void testExcelExportToBytes() {
        // 准备测试数据
        List<TestDataVo> data = new ArrayList<>();
        data.add(new TestDataVo(1L, "测试1", "13800138001", 100));
        data.add(new TestDataVo(2L, "测试2", "13800138002", 200));
        data.add(new TestDataVo(3L, "测试3", "13800138003", 300));

        // 测试导出到字节数组
        byte[] bytes = excelExportService.exportToBytes("测试数据", data, TestDataVo.class);

        // 验证结果
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
        
        // Excel文件头应该是PK开头（ZIP格式）
        assertEquals('P', (char) bytes[0]);
        assertEquals('K', (char) bytes[1]);
    }

    @Test
    public void testPdfExportToBytes() {
        // 准备测试数据
        String[] headers = {"ID", "名称", "电话", "金额"};
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"1", "测试1", "13800138001", "100"});
        data.add(new String[]{"2", "测试2", "13800138002", "200"});
        data.add(new String[]{"3", "测试3", "13800138003", "300"});

        // 测试导出到字节数组
        byte[] bytes = pdfExportService.exportToBytes("测试报表", headers, data);

        // 验证结果
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
        
        // PDF文件头应该是%PDF
        assertEquals('%', (char) bytes[0]);
        assertEquals('P', (char) bytes[1]);
        assertEquals('D', (char) bytes[2]);
        assertEquals('F', (char) bytes[3]);
    }

    @Test
    public void testEmptyDataExport() {
        // 测试空数据导出
        List<TestDataVo> emptyData = new ArrayList<>();
        byte[] excelBytes = excelExportService.exportToBytes("空数据", emptyData, TestDataVo.class);
        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 0);

        String[] headers = {"ID", "名称"};
        List<String[]> emptyPdfData = new ArrayList<>();
        byte[] pdfBytes = pdfExportService.exportToBytes("空报表", headers, emptyPdfData);
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    /**
     * 测试数据VO
     */
    public static class TestDataVo {
        private Long id;
        private String name;
        private String phone;
        private Integer amount;

        public TestDataVo() {
        }

        public TestDataVo(Long id, String name, String phone, Integer amount) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.amount = amount;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }
    }
}
