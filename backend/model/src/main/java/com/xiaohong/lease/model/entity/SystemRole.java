package com.xiaohong.lease.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "后台用户角色")
@TableName("system_role")
@Data
@EqualsAndHashCode(callSuper = false)
public class SystemRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色名称")
    @TableField("role_name")
    private String roleName;

    @Schema(description = "角色编码")
    @TableField("role_code")
    private String roleCode;

    @Schema(description = "描述")
    @TableField("description")
    private String description;

    @Schema(description = "状态")
    @TableField("status")
    private Integer status;
}
