package com.xiaohong.lease.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Schema(description = "后台部门")
@TableName("system_dept")
@Data
@EqualsAndHashCode(callSuper = false)
public class SystemDept extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "上级部门id")
    @TableField("parent_id")
    private Long parentId;

    @Schema(description = "部门名称")
    @TableField("dept_name")
    private String deptName;

    @Schema(description = "部门编码")
    @TableField("dept_code")
    private String deptCode;

    @Schema(description = "排序")
    @TableField("sort")
    private Integer sort;

    @Schema(description = "状态")
    @TableField("status")
    private Integer status;

    @Schema(description = "子节点列表（非数据库字段）")
    @TableField(exist = false)
    private List<SystemDept> children;
}
