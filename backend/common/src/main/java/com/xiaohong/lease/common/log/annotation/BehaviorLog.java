package com.xiaohong.lease.common.log.annotation;

import com.xiaohong.lease.model.enums.BehaviorType;

import java.lang.annotation.*;

/**
 * 用户行为日志注解
 * 标记在Controller方法上，自动记录用户行为日志
 *
 * @author 小红
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BehaviorLog {

    /**
     * 行为类型
     */
    BehaviorType value();

    /**
     * 行为描述
     */
    String desc() default "";

    /**
     * 目标类型
     */
    String targetType() default "";

    /**
     * 目标ID参数名，支持SpEL表达式
     */
    String targetId() default "";

    /**
     * 是否保存请求参数
     */
    boolean saveParam() default true;
}
