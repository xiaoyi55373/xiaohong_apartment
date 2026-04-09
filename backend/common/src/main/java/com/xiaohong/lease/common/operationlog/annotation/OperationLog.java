package com.xiaohong.lease.common.operationlog.annotation;

import com.xiaohong.lease.model.enums.BusinessType;
import com.xiaohong.lease.model.enums.OperatorType;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 标记在Controller方法上，自动记录管理员操作日志
 *
 * @author 小红
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 模块标题
     */
    String title();

    /**
     * 业务类型
     */
    BusinessType businessType() default BusinessType.OTHER;

    /**
     * 操作者类型
     */
    OperatorType operatorType() default OperatorType.SYSTEM_USER;

    /**
     * 是否保存请求参数
     */
    boolean saveParam() default true;

    /**
     * 是否保存响应结果
     */
    boolean saveResult() default false;

    /**
     * 是否记录操作时间
     */
    boolean saveTime() default true;

    /**
     * 排除指定的请求参数（不记录）
     */
    String[] excludeParamNames() default {};

    /**
     * 操作描述，支持SpEL表达式
     */
    String desc() default "";
}
