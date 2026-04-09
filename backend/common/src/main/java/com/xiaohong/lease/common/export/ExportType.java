package com.xiaohong.lease.common.export;

/**
 * 导出类型枚举
 *
 * @author 小红
 * @date 2026-04-03
 */
public enum ExportType {

    /**
     * Excel导出
     */
    EXCEL("excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),

    /**
     * PDF导出
     */
    PDF("pdf", "application/pdf");

    private final String extension;
    private final String contentType;

    ExportType(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public String getExtension() {
        return extension;
    }

    public String getContentType() {
        return contentType;
    }

    public static ExportType fromString(String type) {
        for (ExportType exportType : values()) {
            if (exportType.name().equalsIgnoreCase(type)) {
                return exportType;
            }
        }
        return EXCEL;
    }
}
