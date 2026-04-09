package com.xiaohong.lease.common.circuitbreaker.aspect;

import com.xiaohong.lease.common.circuitbreaker.CircuitBreakerProperties;
import com.xiaohong.lease.common.circuitbreaker.CircuitBreakerRegistry;
import com.xiaohong.lease.common.circuitbreaker.CircuitBreakerState;
import com.xiaohong.lease.common.circuitbreaker.annotation.CircuitBreaker;
import com.xiaohong.lease.common.exception.LeaseException;
import com.xiaohong.lease.common.result.ResultCodeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 熔断器切面
 * 基于AOP实现方法级别的熔断保护
 *
 * @author 小红
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "xiaohong.circuit-breaker", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CircuitBreakerAspect {

    private final CircuitBreakerProperties circuitBreakerProperties;

    @Around("@annotation(circuitBreaker)")
    public Object around(ProceedingJoinPoint point, CircuitBreaker circuitBreaker) throws Throwable {
        if (!circuitBreakerProperties.isEnabled()) {
            return point.proceed();
        }

        String breakerName = buildBreakerName(circuitBreaker, point);
        int failureThreshold = circuitBreaker.failureThreshold() > 0
                ? circuitBreaker.failureThreshold()
                : circuitBreakerProperties.getDefaultFailureThreshold();
        int successThreshold = circuitBreaker.successThreshold() > 0
                ? circuitBreaker.successThreshold()
                : circuitBreakerProperties.getDefaultSuccessThreshold();
        long timeoutDuration = circuitBreaker.timeoutDuration() > 0
                ? circuitBreaker.timeoutDuration()
                : circuitBreakerProperties.getDefaultTimeoutDuration();
        long halfOpenMaxCalls = circuitBreaker.halfOpenMaxCalls() > 0
                ? circuitBreaker.halfOpenMaxCalls()
                : circuitBreakerProperties.getDefaultHalfOpenMaxCalls();

        CircuitBreakerRegistry.CircuitBreakerInstance breaker = CircuitBreakerRegistry.getBreaker(
                breakerName, failureThreshold, successThreshold, timeoutDuration, halfOpenMaxCalls);

        if (!breaker.allowRequest()) {
            log.warn("请求被熔断器拦截, name: {}, state: {}", breakerName, breaker.getState());
            return handleFallback(point, circuitBreaker);
        }

        try {
            Object result = point.proceed();
            breaker.recordSuccess();
            return result;
        } catch (Throwable throwable) {
            // 不记录业务异常（如参数校验失败）为熔断失败
            if (isSystemException(throwable)) {
                breaker.recordFailure();
            }
            throw throwable;
        }
    }

    /**
     * 处理降级逻辑
     */
    private Object handleFallback(ProceedingJoinPoint point, CircuitBreaker circuitBreaker) throws Throwable {
        String fallbackMethod = circuitBreaker.fallbackMethod();
        if (!fallbackMethod.isEmpty()) {
            try {
                return invokeFallbackMethod(point, fallbackMethod);
            } catch (Exception e) {
                log.error("执行降级方法失败, method: {}", fallbackMethod, e);
            }
        }
        throw new LeaseException(ResultCodeEnum.CIRCUIT_BREAKER_OPEN);
    }

    /**
     * 调用降级方法
     */
    private Object invokeFallbackMethod(ProceedingJoinPoint point, String fallbackMethodName) throws Exception {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method targetMethod = signature.getMethod();
        Class<?> targetClass = point.getTarget().getClass();
        Object[] args = point.getArgs();

        // 查找降级方法：允许返回类型兼容，参数列表相同
        Method fallback = findFallbackMethod(targetClass, fallbackMethodName, targetMethod.getParameterTypes());
        if (fallback == null) {
            throw new NoSuchMethodException("未找到降级方法: " + fallbackMethodName);
        }

        fallback.setAccessible(true);
        return fallback.invoke(point.getTarget(), args);
    }

    /**
     * 查找降级方法
     */
    private Method findFallbackMethod(Class<?> targetClass, String fallbackMethodName, Class<?>[] parameterTypes) {
        for (Method method : targetClass.getDeclaredMethods()) {
            if (method.getName().equals(fallbackMethodName)) {
                Class<?>[] methodParams = method.getParameterTypes();
                if (methodParams.length == parameterTypes.length) {
                    boolean match = true;
                    for (int i = 0; i < methodParams.length; i++) {
                        if (!methodParams[i].isAssignableFrom(parameterTypes[i])) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        return method;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 构建熔断器名称
     */
    private String buildBreakerName(CircuitBreaker circuitBreaker, ProceedingJoinPoint point) {
        if (!circuitBreaker.name().isEmpty()) {
            return circuitBreaker.name();
        }
        return point.getTarget().getClass().getSimpleName() + ":" + ((MethodSignature) point.getSignature()).getMethod().getName();
    }

    /**
     * 判断是否为系统异常（需要记录为熔断失败）
     */
    private boolean isSystemException(Throwable throwable) {
        // 业务异常不触发熔断
        if (throwable instanceof LeaseException) {
            return false;
        }
        // 运行时异常和错误触发熔断
        return throwable instanceof RuntimeException || throwable instanceof Error;
    }
}
