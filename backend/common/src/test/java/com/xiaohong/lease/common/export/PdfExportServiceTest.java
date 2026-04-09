package com.xiaohong.lease.common.export;

import jakarta.servlet.http.HttpServletResponse;
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
 * PDF导出服务测试类
 *
 * @author 小红
 * @date 2026-04-03
 */
@ExtendWith(MockitoExtension.class)
class PdfExportServiceTest {

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private PdfExportService pdfExportService;

    @Test
    void testExportToBytes() {
        // 准备测试数据
        String[] headers = {"ID", "名称", "数值"};
        List<String[]> data = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            data.add(new String[]{String.valueOf(i), "测试数据" + i, String.valueOf(i * 100)});
        }

        // 执行导出
        byte[] bytes = pdfExportService.exportToBytes("测试报表", headers, data);

        // 验证结果
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
        // PDF文件头部魔数: %PDF
        assertEquals('%', bytes[0]);
        assertEquals('P', bytes[1]);
        assertEquals('D', bytes[2]);
        assertEquals('F', bytes[3]);
    }

    @Test
    void testExportToBytesWithEmptyData() {
        // 准备空数据
        String[] headers = {"ID", "名称"};
        List<String[]> emptyData = new ArrayList<>();

        // 执行导出
        byte[] bytes = pdfExportService.exportToBytes("空数据测试", headers, emptyData);

        // 验证结果 - 空数据也应该能生成有效的PDF文件
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
        assertEquals('%', bytes[0]);
    }

    @Test
    void testExportToBytesWithLargeData() {
        // 准备大量测试数据
        String[] headers = {"ID", "名称", "数值", "描述"};
        List<String[]> data = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            data.add(new String[]{
                String.valueOf(i),
                "测试数据" + i,
                String.valueOf(i * 100),
                "这是第" + i + "条数据的描述信息"
            });
        }

        // 执行导出
        byte[] bytes = pdfExportService.exportToBytes("大数据量测试", headers, data);

        // 验证结果
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }

    @Test
    void testExportToHttpResponse() throws IOException {
        // 准备测试数据
        String[] headers = {"ID", "名称", "数值"};
        List<String[]> data = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            data.add(new String[]{String.valueOf(i), "测试数据" + i, String.valueOf(i * 10)});
        }

        // 模拟HttpServletResponse
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new ServletOutputStreamWrapper(outputStream));

        // 执行导出
        pdfExportService.export(response, "测试文件", "测试报表", headers, data);

        // 验证响应头设置
        verify(response).setContentType(ExportType.PDF.getContentType());
        verify(response).setCharacterEncoding("utf-8");
        verify(response).setHeader(anyString(), anyString());

        // 验证输出流中有数据
        byte[] bytes = outputStream.toByteArray();
        assertTrue(bytes.length > 0);
        assertEquals('%', bytes[0]);
    }

    @Test
    void testExportWithNullHeaders() {
        // 准备测试数据
        String[] nullHeaders = null;
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"1", "测试"});

        // 执行导出 - 应该正常处理
        byte[] bytes = pdfExportService.exportToBytes("无表头测试", nullHeaders, data);

        // 验证结果
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }

    @Test
    void testExportWithNullCellData() {
        // 准备测试数据（包含null值）
        String[] headers = {"ID", "名称", "数值"};
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"1", null, "100"});
        data.add(new String[]{"2", "测试", null});

        // 执行导出
        byte[] bytes = pdfExportService.exportToBytes("空值测试", headers, data);

        // 验证结果
        assertNotNull(bytes);
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
