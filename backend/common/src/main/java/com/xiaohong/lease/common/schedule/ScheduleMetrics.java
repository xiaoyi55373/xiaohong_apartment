package com.xiaohong.lease.common.schedule;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 定时任务指标收集器
 *
 * @author 小红
 */
@Component
public class ScheduleMetrics {

    /**
     * 任务执行统计
     */
    private final Map<String, TaskMetrics> metricsMap = new ConcurrentHashMap<>();

    /**
     * 记录任务开始
     *
     * @param taskName 任务名称
     */
    public void recordStart(String taskName) {
        TaskMetrics metrics = metricsMap.computeIfAbsent(taskName, k -> new TaskMetrics());
        metrics.setLastStartTime(LocalDateTime.now());
        metrics.getRunningCount().incrementAndGet();
    }

    /**
     * 记录任务完成
     *
     * @param taskName 任务名称
     * @param success 是否成功
     * @param durationMs 执行时长（毫秒）
     */
    public void recordComplete(String taskName, boolean success, long durationMs) {
        TaskMetrics metrics = metricsMap.get(taskName);
        if (metrics == null) {
            return;
        }

        metrics.getRunningCount().decrementAndGet();
        metrics.setLastEndTime(LocalDateTime.now());
        metrics.setLastDurationMs(durationMs);

        if (success) {
            metrics.getSuccessCount().incrementAndGet();
        } else {
            metrics.getFailCount().incrementAndGet();
        }

        // 更新平均执行时间
        long totalCount = metrics.getSuccessCount().get() + metrics.getFailCount().get();
        double currentAvg = metrics.getAvgDurationMs();
        metrics.setAvgDurationMs((currentAvg * (totalCount - 1) + durationMs) / totalCount);
    }

    /**
     * 获取任务指标
     *
     * @param taskName 任务名称
     * @return 任务指标
     */
    public TaskMetrics getMetrics(String taskName) {
        return metricsMap.get(taskName);
    }

    /**
     * 获取所有任务指标
     *
     * @return 所有任务指标
     */
    public Map<String, TaskMetrics> getAllMetrics() {
        return new ConcurrentHashMap<>(metricsMap);
    }

    /**
     * 重置任务指标
     *
     * @param taskName 任务名称
     */
    public void resetMetrics(String taskName) {
        metricsMap.remove(taskName);
    }

    /**
     * 重置所有任务指标
     */
    public void resetAllMetrics() {
        metricsMap.clear();
    }

    /**
     * 任务指标数据类
     */
    @Data
    public static class TaskMetrics {
        /**
         * 成功次数
         */
        private AtomicLong successCount = new AtomicLong(0);

        /**
         * 失败次数
         */
        private AtomicLong failCount = new AtomicLong(0);

        /**
         * 正在运行的任务数
         */
        private AtomicLong runningCount = new AtomicLong(0);

        /**
         * 最后开始时间
         */
        private LocalDateTime lastStartTime;

        /**
         * 最后结束时间
         */
        private LocalDateTime lastEndTime;

        /**
         * 最后执行时长（毫秒）
         */
        private long lastDurationMs;

        /**
         * 平均执行时长（毫秒）
         */
        private double avgDurationMs;

        /**
         * 获取总执行次数
         */
        public long getTotalCount() {
            return successCount.get() + failCount.get();
        }

        /**
         * 获取成功率
         */
        public double getSuccessRate() {
            long total = getTotalCount();
            return total > 0 ? (double) successCount.get() / total * 100 : 0;
        }
    }
}
