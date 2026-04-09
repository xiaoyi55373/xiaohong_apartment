package com.xiaohong.lease.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务线程池配置
 * 
 * 用于定时任务等异步操作的线程池
 *
 * @author 小红
 */
@Configuration
@EnableAsync
public class AsyncTaskConfiguration {

    private static final Logger log = LoggerFactory.getLogger(AsyncTaskConfiguration.class);

    /**
     * 定时任务执行线程池
     */
    @Bean("taskExecutor")
    public Executor taskExecutor() {
        log.info("初始化定时任务执行线程池...");
        
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(5);
        // 最大线程数
        executor.setMaxPoolSize(20);
        // 队列容量
        executor.setQueueCapacity(100);
        // 线程名称前缀
        executor.setThreadNamePrefix("schedule-task-");
        // 拒绝策略：由调用线程处理
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间（秒）
        executor.setAwaitTerminationSeconds(60);
        // 初始化
        executor.initialize();
        
        return executor;
    }

    /**
     * 通知发送线程池
     */
    @Bean("notificationExecutor")
    public Executor notificationExecutor() {
        log.info("初始化通知发送线程池...");
        
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("notification-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        
        return executor;
    }
}
