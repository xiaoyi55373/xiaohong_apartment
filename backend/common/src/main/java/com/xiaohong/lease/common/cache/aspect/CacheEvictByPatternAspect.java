package com.xiaohong.lease.common.cache.aspect;

import com.xiaohong.lease.common.cache.CacheService;
import com.xiaohong.lease.common.cache.annotation.CacheEvictByPattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 批量清除缓存切面
 * 处理@CacheEvictByPattern注解
 * 
 * @author 小红
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CacheEvictByPatternAspect {

    private final CacheService cacheService;
    private final SpelExpressionParser parser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Before("@annotation(com.xiaohong.lease.common.cache.annotation.CacheEvictByPattern)")
    public void before(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CacheEvictByPattern annotation = method.getAnnotation(CacheEvictByPattern.class);

        if (annotation.beforeInvocation()) {
            evictByPattern(joinPoint, annotation);
        }
    }

    @AfterReturning("@annotation(com.xiaohong.lease.common.cache.annotation.CacheEvictByPattern)")
    public void afterReturning(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CacheEvictByPattern annotation = method.getAnnotation(CacheEvictByPattern.class);

        if (!annotation.beforeInvocation()) {
            evictByPattern(joinPoint, annotation);
        }
    }

    private void evictByPattern(JoinPoint joinPoint, CacheEvictByPattern annotation) {
        String[] patterns = annotation.patterns();
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 创建SpEL上下文
        EvaluationContext context = new StandardEvaluationContext();
        String[] paramNames = parameterNameDiscoverer.getParameterNames(method);
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }

        // 处理每个pattern
        for (String pattern : patterns) {
            try {
                // 解析SpEL表达式
                String resolvedPattern = parser.parseExpression(pattern).getValue(context, String.class);
                if (resolvedPattern != null) {
                    cacheService.deleteByPattern(resolvedPattern);
                    log.debug("根据模式清除缓存, pattern: {}", resolvedPattern);
                }
            } catch (Exception e) {
                log.error("解析缓存pattern失败, pattern: {}", pattern, e);
            }
        }
    }
}
