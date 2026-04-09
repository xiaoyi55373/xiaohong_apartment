package com.xiaohong.lease.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Schema(description = "后台菜单权限")
@TableName("system_menu")
@Data
@EqualsAndHashCode(callSuper = false)
public class SystemMenu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "上级id")
    @TableField("parent_id")
    private Long parentId;

    @Schema(description = "菜单标题")
    @TableField("title")
    private String title;

    @Schema(description = "组件名称")
    @TableField("component")
    private String component;

    @Schema(description = "路由地址")
    @TableField("path")
    private String path;

    @Schema(description = "类型")
    @TableField("type")
    private Integer type;

    @Schema(description = "图标")
    @TableField("icon")
    private String icon;

    @Schema(description = "排序")
    @TableField("sort")
    private Integer sort;

    @Schema(description = "状态")
    @TableField("status")
    private Integer status;

    @Schema(description = "子节点列表（非数据库字段）")
    @TableField(exist = false)
    private List<SystemMenu> children;
}
