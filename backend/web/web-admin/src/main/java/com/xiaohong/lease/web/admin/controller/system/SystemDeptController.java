package com.xiaohong.lease.web.admin.controller.system;

import com.xiaohong.lease.common.annotation.CheckPermission;
import com.xiaohong.lease.common.operationlog.annotation.OperationLog;
import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.model.enums.BusinessType;
import com.xiaohong.lease.model.entity.SystemDept;
import com.xiaohong.lease.model.enums.OperatorType;
import com.xiaohong.lease.web.admin.service.SystemDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "后台部门管理")
@RestController
@RequestMapping("/admin/system/dept")
public class SystemDeptController {

    @Autowired
    private SystemDeptService service;

    @Operation(summary = "获取全部部门节点（树形）")
    @GetMapping("listAsTree")
    public Result<List<SystemDept>> listAsTree() {
        List<SystemDept> list = service.listAsTree();
        return Result.ok(list);
    }

    @CheckPermission
    @OperationLog(title = "部门管理", businessType = BusinessType.INSERT, operatorType = OperatorType.SYSTEM_USER,
                  desc = "'新增部门: ' + #systemDept.name")
    @Operation(summary = "新增部门")
    @PostMapping("save")
    public Result save(@RequestBody SystemDept systemDept) {
        service.save(systemDept);
        return Result.ok();
    }

    @CheckPermission
    @OperationLog(title = "部门管理", businessType = BusinessType.UPDATE, operatorType = OperatorType.SYSTEM_USER,
                  desc = "'更新部门: ' + #systemDept.name")
    @Operation(summary = "更新部门")
    @PutMapping("update")
    public Result update(@RequestBody SystemDept systemDept) {
        service.updateById(systemDept);
        return Result.ok();
    }

    @CheckPermission
    @OperationLog(title = "部门管理", businessType = BusinessType.DELETE, operatorType = OperatorType.SYSTEM_USER,
                  desc = "'删除部门ID: ' + #id")
    @DeleteMapping("remove/{id}")
    @Operation(summary = "根据id删除部门")
    public Result removeById(@PathVariable Long id) {
        service.removeById(id);
        return Result.ok();
    }

    @CheckPermission
    @OperationLog(title = "部门管理", businessType = BusinessType.UPDATE, operatorType = OperatorType.SYSTEM_USER,
                  desc = "'更新部门状态: ' + #id + ' -> ' + #status")
    @Operation(summary = "更新部门状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        service.updateStatus(id, status);
        return Result.ok();
    }
}
