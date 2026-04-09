package com.xiaohong.lease.common.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Excel导出服务测试类
 *
 * @author 小红
 * @date 2026-04-03
 */
@ExtendWith(MockitoExtension.class)
class ExcelExportServiceTest {

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private ExcelExportService excelExportService;

    @Data
    public static class TestData {
        @ExcelProperty(value = "ID", index = 0)
        @ColumnWidth(10)
        private Long id;

        @ExcelProperty(value = "名称", index = 1)
        @ColumnWidth(20)
        private String name;

        @ExcelProperty(value = "数值", index = 2)
        @ColumnWidth(15)
        private Integer value;
    }

    @Test
    void testExportToBytes() {
        // 准备测试数据
        List<TestData> dataList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            TestData data = new TestData();
            data.setId((long) i);
            data.setName("测试数据" + i);
            data.setValue(i * 100);
            dataList.add(data);
        }

        // 执行导出
        byte[] bytes = excelExportService.exportToBytes("测试Sheet", dataList, TestData.class);

        // 验证结果
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
        // Excel文件头部魔数: 50 4B 03 04 (ZIP格式)
        assertEquals(0x50, bytes[0] & 0xFF);
        assertEquals(0x4B, bytes[1] & 0xFF);
    }

    @Test
    void testExportToBytesWithEmptyData() {
        // 准备空数据
        List<TestData> emptyList = new ArrayList<>();

        // 执行导出
        byte[] bytes = excelExportService.exportToBytes("测试Sheet", emptyList, TestData.class);

        // 验证结果 - 空数据也应该能生成有效的Excel文件
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }

    @Test
    void testExportToBytesWithLargeData() {
        // 准备大量测试数据
        List<TestData> dataList = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            TestData data = new TestData();
            data.setId((long) i);
            data.setName("测试数据" + i);
            data.setValue(i);
            dataList.add(data);
        }

        // 执行导出
        byte[] bytes = excelExportService.exportToBytes("大数据测试", dataList, TestData.class);

        // 验证结果
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }

    @Test
    void testExportToHttpResponse() throws IOException {
        // 准备测试数据
        List<TestData> dataList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            TestData data = new TestData();
            data.setId((long) i);
            data.setName("测试数据" + i);
            data.setValue(i * 10);
            dataList.add(data);
        }

        // 模拟HttpServletResponse
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new ServletOutputStreamWrapper(outputStream));

        // 执行导出
        excelExportService.export(response, "测试文件", "测试Sheet", dataList, TestData.class);

        // 验证响应头设置
        verify(response).setContentType(ExportType.EXCEL.getContentType());
        verify(response).setCharacterEncoding("utf-8");
        verify(response).setHeader(anyString(), anyString());

        // 验证输出流中有数据
        byte[] bytes = outputStream.toByteArray();
        assertTrue(bytes.length > 0);
    }

    @Test
    void testExportWithDefaultSheetName() throws IOException {
        // 准备测试数据
        List<TestData> dataList = new ArrayList<>();
        TestData data = new TestData();
        data.setId(1L);
        data.setName("测试");
        data.setValue(100);
        dataList.add(data);

        // 模拟HttpServletResponse
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new ServletOutputStreamWrapper(outputStream));

        // 执行导出（使用默认Sheet名称）
        excelExportService.export(response, "测试文件", dataList, TestData.class);

        // 验证输出流中有数据
        byte[] bytes = outputStream.toByteArray();
        assertTrue(bytes.length > 0);
    }

    /**
     * 包装类，用于模拟ServletOutputStream
     */
    private static class ServletOutputStreamWrapper extends jakarta.servlet.ServletOutputStream {
        private final OutputStream outputStream;

        public ServletOutputStreamWrapper(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void write(int b) throws IOException {
            outputStream.write(b);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(jakarta.servlet.WriteListener writeListener) {
            // 不需要实现
        }
    }
}
