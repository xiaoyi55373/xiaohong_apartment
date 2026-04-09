package com.xiaohong.lease.web.admin.controller.monitor;

import com.xiaohong.lease.common.config.PerformanceConfig;
import com.xiaohong.lease.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 性能监控控制器
 * 提供系统性能指标查询接口
 * 
 * @author 小红
 */
@Slf4j
@Tag(name = "性能监控管理")
@RestController
@RequestMapping("/admin/monitor")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceConfig.DatabaseHealthIndicator databaseHealthIndicator;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 获取系统整体性能指标
     */
    @Operation(summary = "获取系统性能指标")
    @GetMapping("/metrics")
    public Result<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // JVM内存信息
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        
        Map<String, Object> memoryInfo = new HashMap<>();
        memoryInfo.put("heapInit", heapMemoryUsage.getInit() / 1024 / 1024 + " MB");
        memoryInfo.put("heapUsed", heapMemoryUsage.getUsed() / 1024 / 1024 + " MB");
        memoryInfo.put("heapCommitted", heapMemoryUsage.getCommitted() / 1024 / 1024 + " MB");
        memoryInfo.put("heapMax", heapMemoryUsage.getMax() / 1024 / 1024 + " MB");
        memoryInfo.put("nonHeapUsed", nonHeapMemoryUsage.getUsed() / 1024 / 1024 + " MB");
        
        metrics.put("memory", memoryInfo);
        
        // JVM运行时信息
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        Map<String, Object> runtimeInfo = new HashMap<>();
        runtimeInfo.put("vmName", runtimeMXBean.getVmName());
        runtimeInfo.put("vmVersion", runtimeMXBean.getVmVersion());
        runtimeInfo.put("uptime", runtimeMXBean.getUptime() / 1000 + " seconds");
        
        metrics.put("runtime", runtimeInfo);
        
        // 系统信息
        Properties props = System.getProperties();
        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("osName", props.getProperty("os.name"));
        systemInfo.put("osVersion", props.getProperty("os.version"));
        systemInfo.put("javaVersion", props.getProperty("java.version"));
        systemInfo.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        
        metrics.put("system", systemInfo);
        
        return Result.ok(metrics);
    }

    /**
     * 获取数据库连接池状态
     */
    @Operation(summary = "获取数据库连接池状态")
    @GetMapping("/db-pool")
    public Result<Map<String, Object>> getDbPoolStatus() {
        Map<String, Object> status = new HashMap<>();
        
        // 数据库健康状态
        boolean isHealthy = databaseHealthIndicator.isHealthy();
        status.put("healthy", isHealthy);
        
        // 连接池状态
        PerformanceConfig.PoolStatus poolStatus = databaseHealthIndicator.getPoolStatus();
        if (poolStatus != null) {
            Map<String, Object> poolInfo = new HashMap<>();
            poolInfo.put("activeConnections", poolStatus.activeConnections());
            poolInfo.put("idleConnections", poolStatus.idleConnections());
            poolInfo.put("totalConnections", poolStatus.totalConnections());
            poolInfo.put("threadsAwaitingConnection", poolStatus.threadsAwaitingConnection());
            
            // 计算使用率
            double usageRate = poolStatus.totalConnections() > 0 
                ? (double) poolStatus.activeConnections() / poolStatus.totalConnections() * 100 
                : 0;
            poolInfo.put("usageRate", String.format("%.2f%%", usageRate));
            
            status.put("pool", poolInfo);
        }
        
        return Result.ok(status);
    }

    /**
     * 获取Redis状态
     */
    @Operation(summary = "获取Redis状态")
    @GetMapping("/redis")
    public Result<Map<String, Object>> getRedisStatus() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            // 测试Redis连接
            redisTemplate.opsForValue().get("test");
            status.put("connected", true);
            
            // 获取Redis信息
            Properties info = redisTemplate.execute(
                connection -> connection.serverCommands().info(), 
                false
            );
            
            if (info != null) {
                Map<String, Object> redisInfo = new HashMap<>();
                redisInfo.put("redisVersion", info.getProperty("redis_version"));
                redisInfo.put("usedMemory", info.getProperty("used_memory_human"));
                redisInfo.put("connectedClients", info.getProperty("connected_clients"));
                redisInfo.put("totalCommandsProcessed", info.getProperty("total_commands_processed"));
                
                status.put("info", redisInfo);
            }
        } catch (Exception e) {
            status.put("connected", false);
            status.put("error", e.getMessage());
        }
        
        return Result.ok(status);
    }

    /**
     * 健康检查
     */
    @Operation(summary = "健康检查")
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        
        // 数据库健康
        health.put("database", databaseHealthIndicator.isHealthy() ? "UP" : "DOWN");
        
        // Redis健康
        try {
            redisTemplate.opsForValue().get("test");
            health.put("redis", "UP");
        } catch (Exception e) {
            health.put("redis", "DOWN");
        }
        
        // 整体状态
        boolean allUp = health.values().stream().allMatch(v -> "UP".equals(v));
        health.put("status", allUp ? "UP" : "DOWN");
        
        return Result.ok(health);
    }
}
