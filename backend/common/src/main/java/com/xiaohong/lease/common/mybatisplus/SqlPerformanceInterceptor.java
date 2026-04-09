package com.xiaohong.lease.common.mybatisplus;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.sql.Statement;
import java.util.Properties;

/**
 * SQL性能监控拦截器
 * 用于监控SQL执行时间，记录慢查询
 * 
 * @author 小红
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "xiaohong.performance", name = "sql-monitor-enabled", havingValue = "true", matchIfMissing = true)
@Intercepts({
    @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
    @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
    @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})
})
public class SqlPerformanceInterceptor implements Interceptor {

    /**
     * 慢查询阈值（毫秒）
     */
    private static final long SLOW_QUERY_THRESHOLD = 1000;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        
        try {
            result = invocation.proceed();
            return result;
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // 获取SQL语句
            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
            String sql = statementHandler.getBoundSql().getSql().replaceAll("\\s+", " ").trim();
            
            // 记录慢查询
            if (duration > SLOW_QUERY_THRESHOLD) {
                log.warn("[慢查询] 执行时间: {}ms, SQL: {}", duration, sql);
            }
            
            // 记录所有SQL（DEBUG级别）
            if (log.isDebugEnabled()) {
                log.debug("[SQL执行] 耗时: {}ms, SQL: {}", duration, sql);
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 可以在这里读取配置属性
    }
}
