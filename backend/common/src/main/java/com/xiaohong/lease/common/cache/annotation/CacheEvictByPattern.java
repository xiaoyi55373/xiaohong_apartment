package com.xiaohong.lease.common.cache.annotation;

import java.lang.annotation.*;

/**
 * 根据模式清除缓存注解
 * 用于在方法执行后根据Key模式批量清除缓存
 * 
 * @author 小红
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheEvictByPattern {

    /**
     * 缓存Key模式
     * 支持SpEL表达式，如 "apartment:*:{#id}"
     */
    String[] patterns();

    /**
     * 是否在方法执行前清除缓存
     * 默认false，在方法执行后清除
     */
    boolean beforeInvocation() default false;
}
