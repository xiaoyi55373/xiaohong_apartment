package com.xiaohong.lease.web.admin.controller.system;

import com.xiaohong.lease.common.annotation.CheckPermission;
import com.xiaohong.lease.common.result.Result;
import com.xiaohong.lease.model.entity.SystemLoginLog;
import com.xiaohong.lease.model.entity.SystemOperationLog;
import com.xiaohong.lease.web.admin.service.SystemLoginLogService;
import com.xiaohong.lease.web.admin.service.SystemOperationLogService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Tag(name = "后台日志管理")
@RestController
@RequestMapping("/admin/system")
public class SystemLogController {

    @Autowired
    private SystemOperationLogService operationLogService;

    @Autowired
    private SystemLoginLogService loginLogService;

    @Operation(summary = "获取后台操作日志列表")
    @GetMapping("operationLog/{current}/{size}")
    public Result<IPage<SystemOperationLog>> operationLogPage(
            @PathVariable long current, 
            @PathVariable long size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String operName,
            @RequestParam(required = false) String createTimeBegin,
            @RequestParam(required = false) String createTimeEnd) {
        IPage<SystemOperationLog> page = new Page<>(current, size);
        IPage<SystemOperationLog> result = operationLogService.pageItem(page, title, operName, createTimeBegin, createTimeEnd);
        return Result.ok(result);
    }

    @Operation(summary = "获取操作日志详情")
    @GetMapping("operationLog/{id}")
    public Result<SystemOperationLog> getOperationLogDetail(@PathVariable Long id) {
        SystemOperationLog log = operationLogService.getById(id);
        return Result.ok(log);
    }

    @CheckPermission
    @Operation(summary = "删除操作日志")
    @DeleteMapping("operationLog/{id}")
    public Result<Boolean> deleteOperationLog(@PathVariable Long id) {
        boolean result = operationLogService.removeById(id);
        return Result.ok(result);
    }

    @CheckPermission
    @Operation(summary = "批量删除操作日志")
    @DeleteMapping("operationLog/batch")
    public Result<Boolean> batchDeleteOperationLog(@RequestBody List<Long> ids) {
        boolean result = operationLogService.removeByIds(ids);
        return Result.ok(result);
    }

    @CheckPermission
    @Operation(summary = "清空操作日志")
    @DeleteMapping("operationLog/clean")
    @Parameter(name = "keepDays", description = "保留最近N天的日志")
    public Result<Boolean> cleanOperationLog(
            @RequestParam(defaultValue = "30") int keepDays) {
        boolean result = operationLogService.cleanLog(keepDays);
        return Result.ok(result);
    }

    @Operation(summary = "获取操作日志统计-按业务类型")
    @GetMapping("operationLog/statistics/businessType")
    public Result<List<Map<String, Object>>> getStatisticsByBusinessType(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime) {
        List<Map<String, Object>> result = operationLogService.getStatisticsByBusinessType(startTime, endTime);
        return Result.ok(result);
    }

    @Operation(summary = "获取操作日志统计-按状态")
    @GetMapping("operationLog/statistics/status")
    public Result<Map<String, Long>> getStatisticsByStatus(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime) {
        Map<String, Long> result = operationLogService.getStatisticsByStatus(startTime, endTime);
        return Result.ok(result);
    }

    @Operation(summary = "获取操作日志趋势")
    @GetMapping("operationLog/statistics/trend")
    public Result<List<Map<String, Object>>> getOperationLogTrend(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime) {
        List<Map<String, Object>> result = operationLogService.getTrendByDate(startTime, endTime);
        return Result.ok(result);
    }

    @Operation(summary = "获取后台登陆日志列表")
    @GetMapping("loginLog/{current}/{size}")
    public Result<IPage<SystemLoginLog>> loginLogPage(
            @PathVariable long current, 
            @PathVariable long size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String createTimeBegin,
            @RequestParam(required = false) String createTimeEnd) {
        IPage<SystemLoginLog> page = new Page<>(current, size);
        IPage<SystemLoginLog> result = loginLogService.pageItem(page, username, createTimeBegin, createTimeEnd);
        return Result.ok(result);
    }

    @CheckPermission
    @Operation(summary = "删除登录日志")
    @DeleteMapping("loginLog/{id}")
    public Result<Boolean> deleteLoginLog(@PathVariable Long id) {
        boolean result = loginLogService.removeById(id);
        return Result.ok(result);
    }

    @CheckPermission
    @Operation(summary = "批量删除登录日志")
    @DeleteMapping("loginLog/batch")
    public Result<Boolean> batchDeleteLoginLog(@RequestBody List<Long> ids) {
        boolean result = loginLogService.removeByIds(ids);
        return Result.ok(result);
    }
}
