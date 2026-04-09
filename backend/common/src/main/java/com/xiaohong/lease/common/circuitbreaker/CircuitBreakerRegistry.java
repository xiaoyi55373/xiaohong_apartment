package com.xiaohong.lease.common.circuitbreaker;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 熔断器注册表
 * 管理所有熔断器实例，基于内存实现
 *
 * @author 小红
 */
@Slf4j
public class CircuitBreakerRegistry {

    private static final ConcurrentHashMap<String, CircuitBreakerInstance> BREAKERS = new ConcurrentHashMap<>();

    /**
     * 获取或创建熔断器实例
     */
    public static CircuitBreakerInstance getBreaker(String name, int failureThreshold, int successThreshold,
                                                     long timeoutDuration, long halfOpenMaxCalls) {
        return BREAKERS.computeIfAbsent(name, k ->
                new CircuitBreakerInstance(name, failureThreshold, successThreshold, timeoutDuration, halfOpenMaxCalls));
    }

    /**
     * 移除熔断器
     */
    public static void removeBreaker(String name) {
        BREAKERS.remove(name);
    }

    /**
     * 清空所有熔断器
     */
    public static void clear() {
        BREAKERS.clear();
    }

    /**
     * 熔断器实例
     */
    public static class CircuitBreakerInstance {

        private final String name;
        private final int failureThreshold;
        private final int successThreshold;
        private final long timeoutDuration;
        private final long halfOpenMaxCalls;

        private volatile CircuitBreakerState state = CircuitBreakerState.CLOSED;
        private final AtomicInteger failureCount = new AtomicInteger(0);
        private final AtomicInteger successCount = new AtomicInteger(0);
        private final AtomicInteger halfOpenCount = new AtomicInteger(0);
        private final AtomicLong lastFailureTime = new AtomicLong(0);

        public CircuitBreakerInstance(String name, int failureThreshold, int successThreshold,
                                      long timeoutDuration, long halfOpenMaxCalls) {
            this.name = name;
            this.failureThreshold = failureThreshold;
            this.successThreshold = successThreshold;
            this.timeoutDuration = timeoutDuration;
            this.halfOpenMaxCalls = halfOpenMaxCalls;
        }

        /**
         * 判断是否允许请求通过
         */
        public synchronized boolean allowRequest() {
            if (state == CircuitBreakerState.OPEN) {
                if (System.currentTimeMillis() - lastFailureTime.get() >= timeoutDuration) {
                    state = CircuitBreakerState.HALF_OPEN;
                    halfOpenCount.set(0);
                    successCount.set(0);
                    failureCount.set(0);
                    log.info("熔断器进入半开状态, name: {}", name);
                    return true;
                }
                return false;
            }

            if (state == CircuitBreakerState.HALF_OPEN) {
                int currentHalfOpenCount = halfOpenCount.incrementAndGet();
                if (currentHalfOpenCount <= halfOpenMaxCalls) {
                    return true;
                }
                halfOpenCount.decrementAndGet();
                return false;
            }

            return true;
        }

        /**
         * 记录成功
         */
        public synchronized void recordSuccess() {
            if (state == CircuitBreakerState.HALF_OPEN) {
                int successes = successCount.incrementAndGet();
                if (successes >= successThreshold) {
                    state = CircuitBreakerState.CLOSED;
                    resetCounts();
                    log.info("熔断器关闭, name: {}", name);
                }
            } else if (state == CircuitBreakerState.CLOSED) {
                failureCount.set(0);
            }
        }

        /**
         * 记录失败
         */
        public synchronized void recordFailure() {
            lastFailureTime.set(System.currentTimeMillis());

            if (state == CircuitBreakerState.HALF_OPEN) {
                state = CircuitBreakerState.OPEN;
                log.warn("熔断器打开(半开状态失败), name: {}", name);
            } else if (state == CircuitBreakerState.CLOSED) {
                int failures = failureCount.incrementAndGet();
                if (failures >= failureThreshold) {
                    state = CircuitBreakerState.OPEN;
                    log.warn("熔断器打开(失败次数达到阈值), name: {}, failures: {}", name, failures);
                }
            }
        }

        /**
         * 重置计数器
         */
        private void resetCounts() {
            failureCount.set(0);
            successCount.set(0);
            halfOpenCount.set(0);
        }

        public CircuitBreakerState getState() {
            return state;
        }

        public String getName() {
            return name;
        }
    }
}
