package com.xiaohong.lease.common.operationlog.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaohong.lease.common.context.LoginUserContext;
import com.xiaohong.lease.common.operationlog.annotation.OperationLog;
import com.xiaohong.lease.common.operationlog.event.OperationLogEvent;
import com.xiaohong.lease.common.operationlog.properties.OperationLogProperties;
import com.xiaohong.lease.model.entity.SystemOperationLog;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志记录切面
 *
 * @author 小红
 */
@Aspect
@Slf4j
public class OperationLogAspect {

    private final OperationLogProperties properties;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 用于在同一次请求中传递操作日志对象
     */
    private static final ThreadLocal<SystemOperationLog> OPERATION_LOG_HOLDER = new ThreadLocal<>();

    public OperationLogAspect(OperationLogProperties properties, ApplicationEventPublisher eventPublisher) {
        this.properties = properties;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 处理前初始化操作日志
     */
    @Before("@annotation(operationLog)")
    public void doBefore(JoinPoint joinPoint, OperationLog operationLog) {
        if (!properties.isEnabled()) {
            return;
        }

        HttpServletRequest request = getRequest();
        if (request == null) {
            return;
        }

        String requestUrl = request.getRequestURI();
        if (isIgnoreUrl(requestUrl)) {
            return;
        }

        SystemOperationLog sysLog = new SystemOperationLog();
        sysLog.setTitle(getTitle(joinPoint, operationLog));
        sysLog.setBusinessType(operationLog.businessType().getCode());
        sysLog.setOperatorType(operationLog.operatorType().getCode());
        sysLog.setRequestMethod(request.getMethod());
        sysLog.setOperUrl(requestUrl);
        sysLog.setOperIp(getClientIp(request));
        sysLog.setOperTime(new Date());
        sysLog.setStatus(1);
        sysLog.setOperationDesc(operationLog.desc());
        sysLog.setStartTime(System.currentTimeMillis());

        // 获取当前登录用户
        if (LoginUserContext.getLoginUser() != null) {
            sysLog.setOperName(LoginUserContext.getLoginUser().getUsername());
            sysLog.setUserId(LoginUserContext.getLoginUser().getUserId());
        }

        // 获取方法名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        sysLog.setMethod(method.getDeclaringClass().getName() + "." + method.getName());

        // 处理请求参数
        if (operationLog.saveParam()) {
            sysLog.setOperParam(buildRequestParam(joinPoint, operationLog));
        }

        OPERATION_LOG_HOLDER.set(sysLog);
    }

    /**
     * 正常返回后处理
     */
    @AfterReturning(pointcut = "@annotation(operationLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, OperationLog operationLog, Object jsonResult) {
        try {
            SystemOperationLog sysLog = OPERATION_LOG_HOLDER.get();
            if (sysLog == null) {
                return;
            }

            sysLog.setStatus(1);
            sysLog.setErrorMsg("");

            // 计算执行时间
            if (sysLog.getStartTime() != null) {
                sysLog.setCostTime(System.currentTimeMillis() - sysLog.getStartTime());
            }

            if (operationLog.saveResult() && jsonResult != null) {
                sysLog.setJsonResult(buildResult(jsonResult));
            }

            eventPublisher.publishEvent(new OperationLogEvent(this, sysLog));
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        } finally {
            OPERATION_LOG_HOLDER.remove();
        }
    }

    /**
     * 异常后处理
     */
    @AfterThrowing(pointcut = "@annotation(operationLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, OperationLog operationLog, Exception e) {
        try {
            SystemOperationLog sysLog = OPERATION_LOG_HOLDER.get();
            if (sysLog == null) {
                return;
            }

            sysLog.setStatus(0);
            sysLog.setErrorMsg(subString(e.getMessage(), 2000));

            // 计算执行时间
            if (sysLog.getStartTime() != null) {
                sysLog.setCostTime(System.currentTimeMillis() - sysLog.getStartTime());
            }

            eventPublisher.publishEvent(new OperationLogEvent(this, sysLog));
        } catch (Exception ex) {
            log.error("记录操作日志失败", ex);
        } finally {
            OPERATION_LOG_HOLDER.remove();
        }
    }

    private String getTitle(JoinPoint joinPoint, OperationLog operationLog) {
        if (!operationLog.title().isEmpty()) {
            return operationLog.title();
        }
        // 尝试从方法上的 @Operation 注解获取
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        io.swagger.v3.oas.annotations.Operation apiOperation = method.getAnnotation(io.swagger.v3.oas.annotations.Operation.class);
        if (apiOperation != null && !apiOperation.summary().isEmpty()) {
            return apiOperation.summary();
        }
        return operationLog.businessType().getName();
    }

    private String buildRequestParam(JoinPoint joinPoint, OperationLog operationLog) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        Map<String, Object> paramMap = new HashMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            if (args[i] != null && !isIgnoreParam(paramNames[i]) && isSerializable(args[i])) {
                // 检查是否在排除列表中
                if (isExcludeParam(paramNames[i], operationLog.excludeParamNames())) {
                    paramMap.put(paramNames[i], "***");
                } else {
                    paramMap.put(paramNames[i], args[i]);
                }
            }
        }

        try {
            String paramJson = objectMapper.writeValueAsString(paramMap);
            return subString(paramJson, properties.getParamMaxLength());
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private String buildResult(Object result) {
        try {
            if (result instanceof com.xiaohong.lease.common.result.Result) {
                String json = objectMapper.writeValueAsString(result);
                return subString(json, properties.getResultMaxLength());
            }
            return null;
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private boolean isIgnoreParam(String paramName) {
        return properties.getIgnoreParams().stream()
                .anyMatch(ignore -> paramName.toLowerCase().contains(ignore.toLowerCase()));
    }

    private boolean isExcludeParam(String paramName, String[] excludeParamNames) {
        return Arrays.asList(excludeParamNames).contains(paramName);
    }

    private boolean isSerializable(Object obj) {
        return !(obj instanceof HttpServletRequest) && !(obj instanceof jakarta.servlet.http.HttpServletResponse);
    }

    private boolean isIgnoreUrl(String url) {
        return properties.getIgnoreUrls().stream().anyMatch(url::startsWith);
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.getRequest();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String subString(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        if (str.length() > maxLength) {
            return str.substring(0, maxLength) + "...";
        }
        return str;
    }
}
