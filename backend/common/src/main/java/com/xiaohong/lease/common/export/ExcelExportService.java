package com.xiaohong.lease.common.export;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Excel导出服务
 *
 * @author 小红
 * @date 2026-04-03
 */
@Slf4j
@Service
public class ExcelExportService {

    /**
     * 导出Excel文件
     *
     * @param response HTTP响应对象
     * @param fileName 文件名（不含扩展名）
     * @param sheetName Sheet名称
     * @param data 数据列表
     * @param clazz 数据类型Class
     * @param <T> 数据类型
     */
    public <T> void export(HttpServletResponse response, String fileName, String sheetName,
                           List<T> data, Class<T> clazz) throws IOException {
        // 设置响应头
        response.setContentType(ExportType.EXCEL.getContentType());
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" 
                + encodedFileName + ".xlsx");

        // 写入Excel
        ExcelWriterBuilder writerBuilder = EasyExcel.write(response.getOutputStream(), clazz)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy());

        // 添加默认样式
        writerBuilder.sheet(sheetName).doWrite(data);

        log.info("Excel导出成功: {}.xlsx, 数据条数: {}", fileName, data.size());
    }

    /**
     * 导出Excel文件（使用默认Sheet名称）
     *
     * @param response HTTP响应对象
     * @param fileName 文件名（不含扩展名）
     * @param data 数据列表
     * @param clazz 数据类型Class
     * @param <T> 数据类型
     */
    public <T> void export(HttpServletResponse response, String fileName,
                           List<T> data, Class<T> clazz) throws IOException {
        export(response, fileName, "数据", data, clazz);
    }

    /**
     * 导出Excel到字节数组（用于生成文件或邮件附件）
     *
     * @param sheetName Sheet名称
     * @param data 数据列表
     * @param clazz 数据类型Class
     * @param <T> 数据类型
     * @return Excel文件字节数组
     */
    public <T> byte[] exportToBytes(String sheetName, List<T> data, Class<T> clazz) {
        try (java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream()) {
            EasyExcel.write(outputStream, clazz)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet(sheetName)
                    .doWrite(data);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Excel导出到字节数组失败", e);
            throw new RuntimeException("Excel导出失败", e);
        }
    }
}
