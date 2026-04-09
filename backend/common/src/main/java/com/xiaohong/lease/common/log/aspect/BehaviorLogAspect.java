package com.xiaohong.lease.common.log.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaohong.lease.common.context.LoginUserContext;
import com.xiaohong.lease.common.log.annotation.BehaviorLog;
import com.xiaohong.lease.common.log.properties.BehaviorLogProperties;
import com.xiaohong.lease.model.entity.UserBehaviorLog;
import com.xiaohong.lease.model.enums.BehaviorType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户行为日志切面
 *
 * @author 小红
 */
@Aspect
@Slf4j
public class BehaviorLogAspect {

    private final BehaviorLogProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SpelExpressionParser parser = new SpelExpressionParser();

    public BehaviorLogAspect(BehaviorLogProperties properties) {
        this.properties = properties;
    }

    @Around("@annotation(behaviorLog)")
    public Object around(ProceedingJoinPoint joinPoint, BehaviorLog behaviorLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Throwable throwable = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable t) {
            throwable = t;
            throw t;
        } finally {
            try {
                if (properties.isEnabled()) {
                    saveBehaviorLog(joinPoint, behaviorLog, startTime, throwable);
                }
            } catch (Exception e) {
                log.error("保存用户行为日志失败", e);
            }
        }
    }

    private void saveBehaviorLog(ProceedingJoinPoint joinPoint, BehaviorLog behaviorLog,
                                  long startTime, Throwable throwable) {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return;
        }

        String requestUrl = request.getRequestURI();
        if (isIgnoreUrl(requestUrl)) {
            return;
        }

        UserBehaviorLog userBehaviorLog = new UserBehaviorLog();
        userBehaviorLog.setBehaviorType(behaviorLog.value());
        userBehaviorLog.setBehaviorDesc(buildDesc(behaviorLog));
        userBehaviorLog.setTargetType(behaviorLog.targetType());
        userBehaviorLog.setTargetId(resolveTargetId(joinPoint, behaviorLog));
        userBehaviorLog.setRequestUrl(requestUrl);
        userBehaviorLog.setRequestParam(buildRequestParam(joinPoint, behaviorLog));
        userBehaviorLog.setUserIp(getClientIp(request));
        userBehaviorLog.setUserAgent(request.getHeader("User-Agent"));
        userBehaviorLog.setDeviceType(getDeviceType(request));
        userBehaviorLog.setSourcePage(request.getHeader("Referer"));
        userBehaviorLog.setBehaviorTime(new Date());

        if (LoginUserContext.getLoginUser() != null) {
            userBehaviorLog.setUserId(LoginUserContext.getLoginUser().getUserId());
        }

        // 构建额外数据
        Map<String, Object> extraData = new HashMap<>();
        extraData.put("costTime", System.currentTimeMillis() - startTime);
        if (throwable != null) {
            extraData.put("errorMsg", throwable.getMessage());
        }
        try {
            userBehaviorLog.setExtraData(objectMapper.writeValueAsString(extraData));
        } catch (JsonProcessingException e) {
            log.warn("序列化额外数据失败", e);
        }

        // 发布事件或存入队列（这里通过ThreadLocal或事件机制交给具体服务处理）
        BehaviorLogHolder.setCurrentLog(userBehaviorLog);
    }

    private String buildDesc(BehaviorLog behaviorLog) {
        if (!behaviorLog.desc().isEmpty()) {
            return behaviorLog.desc();
        }
        return behaviorLog.value().getName();
    }

    private Long resolveTargetId(ProceedingJoinPoint joinPoint, BehaviorLog behaviorLog) {
        if (behaviorLog.targetId().isEmpty()) {
            return null;
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        try {
            Object value = parser.parseExpression(behaviorLog.targetId()).getValue(context);
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            if (value instanceof String) {
                return Long.valueOf((String) value);
            }
        } catch (Exception e) {
            log.warn("解析目标ID失败, expression={}", behaviorLog.targetId(), e);
        }
        return null;
    }

    private String buildRequestParam(ProceedingJoinPoint joinPoint, BehaviorLog behaviorLog) {
        if (!behaviorLog.saveParam()) {
            return null;
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        Map<String, Object> paramMap = new HashMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            if (args[i] != null && !isIgnoreParam(paramNames[i]) && isSerializable(args[i])) {
                paramMap.put(paramNames[i], args[i]);
            }
        }

        try {
            String paramJson = objectMapper.writeValueAsString(paramMap);
            if (paramJson.length() > properties.getParamMaxLength()) {
                return paramJson.substring(0, properties.getParamMaxLength()) + "...";
            }
            return paramJson;
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private boolean isIgnoreParam(String paramName) {
        return properties.getIgnoreParams().stream()
                .anyMatch(ignore -> paramName.toLowerCase().contains(ignore.toLowerCase()));
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

    private String getDeviceType(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "unknown";
        }
        String ua = userAgent.toLowerCase();
        if (ua.contains("micromessenger")) {
            return "wechat";
        }
        if (ua.contains("android")) {
            return "android";
        }
        if (ua.contains("iphone") || ua.contains("ipad")) {
            return "ios";
        }
        if (ua.contains("windows")) {
            return "pc";
        }
        if (ua.contains("macintosh")) {
            return "mac";
        }
        return "other";
    }

    /**
     * 行为日志ThreadLocal持有者
     */
    public static class BehaviorLogHolder {
        private static final ThreadLocal<UserBehaviorLog> CURRENT_LOG = new ThreadLocal<>();

        public static void setCurrentLog(UserBehaviorLog log) {
            CURRENT_LOG.set(log);
        }

        public static UserBehaviorLog getCurrentLog() {
            return CURRENT_LOG.get();
        }

        public static void clear() {
            CURRENT_LOG.remove();
        }
    }
}
