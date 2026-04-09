package com.xiaohong.lease.web.admin.controller.system;

import com.xiaohong.lease.common.annotation.CheckPermission;
import com.xiaohong.lease.common.operationlog.annotation.OperationLog;
import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.model.enums.BusinessType;
import com.xiaohong.lease.model.entity.SystemRole;
import com.xiaohong.lease.model.enums.OperatorType;
import com.xiaohong.lease.web.admin.service.SystemRoleService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "后台用户角色管理")
@RestController
@RequestMapping("/admin/system/role")
public class SystemRoleController {

    @Autowired
    private SystemRoleService service;

    @Operation(summary = "分页获取角色信息")
    @GetMapping("page")
    public Result<IPage<SystemRole>> page(@RequestParam long current, @RequestParam long size) {
        IPage<SystemRole> page = new Page<>(current, size);
        IPage<SystemRole> list = service.page(page);
        return Result.ok(list);
    }

    @Operation(summary = "获取全部角色列表")
    @GetMapping("list")
    public Result<List<SystemRole>> list() {
        List<SystemRole> list = service.list();
        return Result.ok(list);
    }

    @CheckPermission
    @OperationLog(title = "角色管理", businessType = BusinessType.INSERT, operatorType = OperatorType.SYSTEM_USER,
                  desc = "'保存或更新角色: ' + #systemRole.name")
    @Operation(summary = "保存或更新角色信息")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody SystemRole systemRole) {
        service.saveOrUpdate(systemRole);
        return Result.ok();
    }

    @CheckPermission
    @OperationLog(title = "角色管理", businessType = BusinessType.DELETE, operatorType = OperatorType.SYSTEM_USER,
                  desc = "'删除角色ID: ' + #id")
    @DeleteMapping("removeById")
    @Operation(summary = "根据id删除角色")
    public Result removeById(@RequestParam Long id) {
        service.removeById(id);
        return Result.ok();
    }

    @CheckPermission
    @OperationLog(title = "角色管理", businessType = BusinessType.DELETE, operatorType = OperatorType.SYSTEM_USER,
                  desc = "'批量删除角色'")
    @Operation(summary = "批量删除角色")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> ids) {
        service.removeByIds(ids);
        return Result.ok();
    }

    @CheckPermission
    @OperationLog(title = "角色管理", businessType = BusinessType.ASSIGN, operatorType = OperatorType.SYSTEM_USER,
                  desc = "'给角色分配权限: ' + #id")
    @Operation(summary = "给某个角色分配权限")
    @PostMapping("updateMenuListById")
    public Result updateMenuListById(@RequestParam Long id, @RequestBody List<Long> menuIdList) {
        service.updateMenuListById(id, menuIdList);
        return Result.ok();
    }
}
