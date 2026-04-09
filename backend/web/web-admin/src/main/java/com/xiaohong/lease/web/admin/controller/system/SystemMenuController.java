package com.xiaohong.lease.web.admin.controller.system;

import com.xiaohong.lease.common.annotation.CheckPermission;
import com.xiaohong.lease.common.operationlog.annotation.OperationLog;
import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.model.enums.BusinessType;
import com.xiaohong.lease.model.entity.SystemMenu;
import com.xiaohong.lease.model.enums.OperatorType;
import com.xiaohong.lease.web.admin.service.SystemMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "后台菜单权限管理")
@RestController
@RequestMapping("/admin/system/menu")
public class SystemMenuController {

    @Autowired
    private SystemMenuService service;

    @Operation(summary = "获取权限菜单列表（树形）")
    @GetMapping("listAsTree")
    public Result<List<SystemMenu>> listAsTree() {
        List<SystemMenu> list = service.listAsTree();
        return Result.ok(list);
    }

    @Operation(summary = "查看某个角色权限列表")
    @GetMapping("listAsTreeByRoleId")
    public Result<List<SystemMenu>> listAsTreeByRoleId(@RequestParam Long id) {
        List<SystemMenu> list = service.listByRoleId(id);
        return Result.ok(list);
    }

    @CheckPermission
    @OperationLog(title = "菜单管理", businessType = BusinessType.INSERT, operatorType = OperatorType.SYSTEM_USER,
                  desc = "'新增或更新菜单: ' + #systemMenu.name")
    @Operation(summary = "新增或更新菜单")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody SystemMenu systemMenu) {
        service.saveOrUpdate(systemMenu);
        return Result.ok();
    }

    @CheckPermission
    @OperationLog(title = "菜单管理", businessType = BusinessType.DELETE, operatorType = OperatorType.SYSTEM_USER,
                  desc = "'删除菜单ID: ' + #id")
    @DeleteMapping("removeById")
    @Operation(summary = "根据id删除菜单")
    public Result removeById(@RequestParam Long id) {
        service.removeById(id);
        return Result.ok();
    }
}
