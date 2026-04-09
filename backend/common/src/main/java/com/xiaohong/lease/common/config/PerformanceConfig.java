package com.xiaohong.lease.common.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * 性能监控配置类
 * 配置数据源监控和性能指标收集
 * 
 * @author 小红
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "xiaohong.performance", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PerformanceConfig {

    /**
     * 配置HikariCP连接池监控
     * 使用BeanPostProcessor后处理Spring Boot自动配置的DataSource
     */
    @Bean
    public BeanPostProcessor hikariDataSourcePostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                if (bean instanceof HikariDataSource hikariDataSource) {
                    // 配置连接池监控
                    hikariDataSource.setMetricRegistry(null); // 可以接入Micrometer
                    hikariDataSource.setHealthCheckRegistry(null);
                    
                    // 设置连接池名称
                    if (hikariDataSource.getPoolName() == null || hikariDataSource.getPoolName().isEmpty()) {
                        hikariDataSource.setPoolName("XiaoHongHikariPool");
                    }
                    
                    // 启用JMX监控
                    hikariDataSource.setRegisterMbeans(true);
                    
                    log.info("HikariCP连接池监控已启用");
                }
                return bean;
            }
        };
    }

    /**
     * 数据库健康检查
     */
    @Bean
    public DatabaseHealthIndicator databaseHealthIndicator(DataSource dataSource) {
        return new DatabaseHealthIndicator(dataSource);
    }

    /**
     * 数据库健康指示器
     */
    public static class DatabaseHealthIndicator {
        private final DataSource dataSource;
        private final JdbcTemplate jdbcTemplate;

        public DatabaseHealthIndicator(DataSource dataSource) {
            this.dataSource = dataSource;
            this.jdbcTemplate = new JdbcTemplate(dataSource);
        }

        /**
         * 检查数据库连接状态
         */
        public boolean isHealthy() {
            try {
                jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                return true;
            } catch (Exception e) {
                log.error("数据库健康检查失败", e);
                return false;
            }
        }

        /**
         * 获取连接池状态
         */
        public PoolStatus getPoolStatus() {
            if (dataSource instanceof HikariDataSource hikariDataSource) {
                return new PoolStatus(
                    hikariDataSource.getHikariPoolMXBean().getActiveConnections(),
                    hikariDataSource.getHikariPoolMXBean().getIdleConnections(),
                    hikariDataSource.getHikariPoolMXBean().getTotalConnections(),
                    hikariDataSource.getHikariPoolMXBean().getThreadsAwaitingConnection()
                );
            }
            return null;
        }
    }

    /**
     * 连接池状态
     */
    public record PoolStatus(
        int activeConnections,
        int idleConnections,
        int totalConnections,
        int threadsAwaitingConnection
    ) {}
}
