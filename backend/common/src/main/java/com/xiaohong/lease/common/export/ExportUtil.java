package com.xiaohong.lease.common.export;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

/**
 * 数据导出工具类
 * <p>
 * 支持大数据量分批导出，避免内存溢出
 *
 * @author 小红
 * @date 2026-04-03
 */
@Slf4j
public class ExportUtil {

    /**
     * 默认批次大小
     */
    public static final int DEFAULT_BATCH_SIZE = 1000;

    /**
     * 大数据量分批导出Excel
     * <p>
     * 使用流式写入，避免内存溢出
     *
     * @param response   HTTP响应对象
     * @param fileName   文件名（不含扩展名）
     * @param sheetName  Sheet名称
     * @param clazz      数据类型Class
     * @param totalCount 总数据量
     * @param batchSize  批次大小
     * @param fetcher    数据获取函数（参数：offset, limit，返回：数据列表）
     * @param <T>        数据类型
     */
    public static <T> void exportLargeData(HttpServletResponse response,
                                           String fileName,
                                           String sheetName,
                                           Class<T> clazz,
                                           long totalCount,
                                           int batchSize,
                                           Function<PageParam, List<T>> fetcher) throws IOException {
        // 设置响应头
        response.setContentType(ExportType.EXCEL.getContentType());
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''"
                + encodedFileName + ".xlsx");

        // 创建Excel写入器
        ExcelWriterBuilder writerBuilder = EasyExcel.write(response.getOutputStream(), clazz)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy());

        try (ExcelWriter excelWriter = writerBuilder.build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();

            // 分批写入数据
            long totalPages = (totalCount + batchSize - 1) / batchSize;
            for (int page = 0; page < totalPages; page++) {
                int offset = page * batchSize;
                List<T> data = fetcher.apply(new PageParam(offset, batchSize));
                
                if (data == null || data.isEmpty()) {
                    break;
                }
                
                excelWriter.write(data, writeSheet);
                log.debug("Excel分批导出进度: {}/{} 页", page + 1, totalPages);
            }
        }

        log.info("Excel大数据量导出成功: {}.xlsx, 总数据条数: {}", fileName, totalCount);
    }

    /**
     * 大数据量分批导出Excel（使用默认批次大小）
     *
     * @param response   HTTP响应对象
     * @param fileName   文件名（不含扩展名）
     * @param sheetName  Sheet名称
     * @param clazz      数据类型Class
     * @param totalCount 总数据量
     * @param fetcher    数据获取函数
     * @param <T>        数据类型
     */
    public static <T> void exportLargeData(HttpServletResponse response,
                                           String fileName,
                                           String sheetName,
                                           Class<T> clazz,
                                           long totalCount,
                                           Function<PageParam, List<T>> fetcher) throws IOException {
        exportLargeData(response, fileName, sheetName, clazz, totalCount, DEFAULT_BATCH_SIZE, fetcher);
    }

    /**
     * 分页参数
     */
    public static class PageParam {
        private final int offset;
        private final int limit;

        public PageParam(int offset, int limit) {
            this.offset = offset;
            this.limit = limit;
        }

        public int getOffset() {
            return offset;
        }

        public int getLimit() {
            return limit;
        }
    }

    /**
     * 导出进度回调接口
     */
    @FunctionalInterface
    public interface ExportProgressCallback {
        void onProgress(long current, long total);
    }

    /**
     * 带进度回调的大数据量导出
     *
     * @param response   HTTP响应对象
     * @param fileName   文件名（不含扩展名）
     * @param sheetName  Sheet名称
     * @param clazz      数据类型Class
     * @param totalCount 总数据量
     * @param batchSize  批次大小
     * @param fetcher    数据获取函数
     * @param callback   进度回调函数
     * @param <T>        数据类型
     */
    public static <T> void exportLargeDataWithProgress(HttpServletResponse response,
                                                       String fileName,
                                                       String sheetName,
                                                       Class<T> clazz,
                                                       long totalCount,
                                                       int batchSize,
                                                       Function<PageParam, List<T>> fetcher,
                                                       ExportProgressCallback callback) throws IOException {
        // 设置响应头
        response.setContentType(ExportType.EXCEL.getContentType());
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''"
                + encodedFileName + ".xlsx");

        // 创建Excel写入器
        ExcelWriterBuilder writerBuilder = EasyExcel.write(response.getOutputStream(), clazz)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy());

        try (ExcelWriter excelWriter = writerBuilder.build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();

            // 分批写入数据
            long processedCount = 0;
            int page = 0;
            
            while (processedCount < totalCount) {
                int offset = page * batchSize;
                List<T> data = fetcher.apply(new PageParam(offset, batchSize));
                
                if (data == null || data.isEmpty()) {
                    break;
                }
                
                excelWriter.write(data, writeSheet);
                processedCount += data.size();
                
                if (callback != null) {
                    callback.onProgress(processedCount, totalCount);
                }
                
                log.debug("Excel分批导出进度: {}/{} 条", processedCount, totalCount);
                page++;
            }
        }

        log.info("Excel大数据量导出成功: {}.xlsx, 实际导出条数: {}", fileName, totalCount);
    }
}
