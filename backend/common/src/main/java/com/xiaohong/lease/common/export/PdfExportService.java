package com.xiaohong.lease.common.export;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * PDF导出服务
 *
 * @author 小红
 * @date 2026-04-03
 */
@Slf4j
@Service
public class PdfExportService {

    // 品牌色 - 珊瑚红
    private static final Color BRAND_COLOR = new Color(255, 107, 107);
    private static final Color HEADER_BG_COLOR = new Color(255, 245, 245);
    private static final Color BORDER_COLOR = new Color(255, 200, 200);

    /**
     * 导出PDF文件
     *
     * @param response HTTP响应对象
     * @param fileName 文件名（不含扩展名）
     * @param title 文档标题
     * @param headers 表头数组
     * @param data 数据列表（每行是一个字符串数组）
     */
    public void export(HttpServletResponse response, String fileName, String title,
                       String[] headers, List<String[]> data) throws IOException {
        // 设置响应头
        response.setContentType(ExportType.PDF.getContentType());
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" 
                + encodedFileName + ".pdf");

        // 创建PDF文档
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        
        document.open();
        
        // 添加标题
        addTitle(document, title);
        
        // 添加表格
        addTable(document, headers, data);
        
        // 添加页脚
        addFooter(document);
        
        document.close();
        writer.close();
        
        log.info("PDF导出成功: {}.pdf, 数据条数: {}", fileName, data.size());
    }

    /**
     * 添加标题
     */
    private void addTitle(Document document, String title) throws DocumentException {
        // 使用系统默认字体（支持中文）
        BaseFont bfChinese = null;
        try {
            bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        } catch (Exception e) {
            // 如果系统字体不可用，使用内置字体
            try {
                bfChinese = BaseFont.createFont("Helvetica", "Cp1252", BaseFont.NOT_EMBEDDED);
            } catch (Exception ex) {
                log.warn("无法加载中文字体，PDF可能显示异常");
            }
        }
        
        Font titleFont = new Font(bfChinese, 18, Font.BOLD, BRAND_COLOR);
        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        titlePara.setSpacingAfter(20);
        document.add(titlePara);
        
        // 添加分隔线
        LineSeparator line = new LineSeparator();
        line.setLineColor(BRAND_COLOR);
        line.setLineWidth(2);
        document.add(line);
        document.add(Chunk.NEWLINE);
    }

    /**
     * 添加表格
     */
    private void addTable(Document document, String[] headers, List<String[]> data) 
            throws DocumentException {
        if (headers == null || headers.length == 0) {
            return;
        }
        
        // 创建表格
        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);
        
        // 设置表头样式
        BaseFont bfChinese = null;
        try {
            bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        } catch (Exception e) {
            try {
                bfChinese = BaseFont.createFont("Helvetica", "Cp1252", BaseFont.NOT_EMBEDDED);
            } catch (Exception ex) {
                // 忽略
            }
        }
        
        Font headerFont = new Font(bfChinese, 10, Font.BOLD, Color.WHITE);
        Font cellFont = new Font(bfChinese, 9, Font.NORMAL, Color.BLACK);
        
        // 添加表头
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(BRAND_COLOR);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(8);
            cell.setBorderColor(BORDER_COLOR);
            table.addCell(cell);
        }
        
        // 添加数据行
        boolean isEven = false;
        for (String[] row : data) {
            for (int i = 0; i < row.length && i < headers.length; i++) {
                PdfPCell cell = new PdfPCell(new Phrase(row[i] != null ? row[i] : "", cellFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(6);
                cell.setBorderColor(BORDER_COLOR);
                
                // 斑马纹效果
                if (isEven) {
                    cell.setBackgroundColor(HEADER_BG_COLOR);
                }
                
                table.addCell(cell);
            }
            isEven = !isEven;
        }
        
        document.add(table);
    }

    /**
     * 添加页脚
     */
    private void addFooter(Document document) throws DocumentException {
        BaseFont bfChinese = null;
        try {
            bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        } catch (Exception e) {
            try {
                bfChinese = BaseFont.createFont("Helvetica", "Cp1252", BaseFont.NOT_EMBEDDED);
            } catch (Exception ex) {
                // 忽略
            }
        }
        
        Font footerFont = new Font(bfChinese, 8, Font.NORMAL, Color.GRAY);
        Paragraph footer = new Paragraph("小红公寓claw - 让租房更简单 | 导出时间: " 
                + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()), 
                footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20);
        document.add(footer);
    }

    /**
     * 导出PDF到字节数组
     *
     * @param title 文档标题
     * @param headers 表头数组
     * @param data 数据列表
     * @return PDF文件字节数组
     */
    public byte[] exportToBytes(String title, String[] headers, List<String[]> data) {
        try (java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            
            document.open();
            addTitle(document, title);
            addTable(document, headers, data);
            addFooter(document);
            document.close();
            writer.close();
            
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("PDF导出到字节数组失败", e);
            throw new RuntimeException("PDF导出失败", e);
        }
    }
}
