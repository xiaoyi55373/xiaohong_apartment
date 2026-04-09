package com.xiaohong.lease.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Schema(description = "后台操作日志")
@TableName("system_operation_log")
@Data
@EqualsAndHashCode(callSuper = false)
public class SystemOperationLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "模块标题")
    @TableField("title")
    private String title;

    @Schema(description = "业务类型")
    @TableField("business_type")
    private Integer businessType;

    @Schema(description = "方法名称")
    @TableField("method")
    private String method;

    @Schema(description = "请求方式")
    @TableField("request_method")
    private String requestMethod;

    @Schema(description = "操作类别")
    @TableField("operator_type")
    private Integer operatorType;

    @Schema(description = "操作人员")
    @TableField("oper_name")
    private String operName;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "请求URL")
    @TableField("oper_url")
    private String operUrl;

    @Schema(description = "主机地址")
    @TableField("oper_ip")
    private String operIp;

    @Schema(description = "请求参数")
    @TableField("oper_param")
    private String operParam;

    @Schema(description = "返回参数")
    @TableField("json_result")
    private String jsonResult;

    @Schema(description = "操作状态（0-失败 1-成功）")
    @TableField("status")
    private Integer status;

    @Schema(description = "错误消息")
    @TableField("error_msg")
    private String errorMsg;

    @Schema(description = "操作时间")
    @TableField("oper_time")
    private Date operTime;

    @Schema(description = "执行耗时（毫秒）")
    @TableField("cost_time")
    private Long costTime;

    @Schema(description = "操作描述")
    @TableField("operation_desc")
    private String operationDesc;

    // 非持久化字段，用于记录开始时间
    @TableField(exist = false)
    private transient Long startTime;
}
