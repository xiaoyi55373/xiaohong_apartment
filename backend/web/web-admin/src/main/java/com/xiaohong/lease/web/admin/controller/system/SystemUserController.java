package com.xiaohong.lease.web.admin.controller.system;


import com.xiaohong.lease.common.annotation.CheckPermission;
import com.xiaohong.lease.common.operationlog.annotation.OperationLog;
import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.model.entity.SystemUser;
import com.xiaohong.lease.model.enums.BaseStatus;
import com.xiaohong.lease.model.enums.BusinessType;
import com.xiaohong.lease.model.enums.OperatorType;
import com.xiaohong.lease.web.admin.service.SystemUserService;
import com.xiaohong.lease.web.admin.vo.system.user.SystemUserItemVo;
import com.xiaohong.lease.web.admin.vo.system.user.SystemUserQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Tag(name = "后台用户信息管理")
@RestController
@RequestMapping("/admin/system/user")
public class SystemUserController {

    @Autowired
    private SystemUserService service;

    @CheckPermission
    @Operation(summary = "保存或更新后台用户信息")
    @PostMapping("saveOrUpdate")
    @OperationLog(title = "用户管理", businessType = BusinessType.INSERT, operatorType = OperatorType.SYSTEM_USER,
                  desc = "'保存或更新用户: ' + #systemUser.name")
    public Result saveOrUpdate(@RequestBody SystemUser systemUser) {
        service.saveOrUpdateSystemUser(systemUser);
        return Result.ok();
    }

    @Operation(summary = "判断后台用户名是否可用")
    @GetMapping("isUserNameAvailable")
    public Result<Boolean> isUsernameExists(@RequestParam(required = false) Long id, @RequestParam String username) {
        LambdaQueryWrapper<SystemUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(id != null, SystemUser::getId, id);
        queryWrapper.eq(SystemUser::getUsername, username);
        long count = service.count(queryWrapper);
        return Result.ok(count == 0);
    }

    @Operation(summary = "根据条件分页查询后台用户列表")
    @GetMapping("page")
    public Result<IPage<SystemUserItemVo>> page(@RequestParam long current, @RequestParam long size, SystemUserQueryVo queryVo) {
        IPage<SystemUser> page = new Page<>(current, size);
        IPage<SystemUserItemVo> list = service.pageItem(page,queryVo);
        return Result.ok(list);
    }

    @CheckPermission
    @DeleteMapping("deleteById")
    @Operation(summary = "根据ID删除后台用户信息")
    @OperationLog(title = "用户管理", businessType = BusinessType.DELETE, operatorType = OperatorType.SYSTEM_USER,
                  desc = "'删除用户ID: ' + #id")
    public Result removeById(@RequestParam Long id) {
        service.removeById(id);
        return Result.ok();
    }

    @Operation(summary = "根据ID查询后台用户信息")
    @GetMapping("getById")
    public Result<SystemUserItemVo> getById(@RequestParam Long id) {
        SystemUserItemVo user = service.getSystemUserById(id);
        return Result.ok(user);
    }

    @CheckPermission
    @Operation(summary = "根据ID修改后台用户状态")
    @PostMapping("updateStatusByUserId")
    @OperationLog(title = "用户管理", businessType = BusinessType.UPDATE, operatorType = OperatorType.SYSTEM_USER,
                  desc = "'修改用户状态: ' + #id + ' -> ' + #status")
    public Result updateStatusByUserId(@RequestParam Long id, @RequestParam BaseStatus status) {
        LambdaUpdateWrapper<SystemUser> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(SystemUser::getId, id);
        queryWrapper.set(SystemUser::getStatus, status);
        service.update(queryWrapper);
        return Result.ok();
    }
}
