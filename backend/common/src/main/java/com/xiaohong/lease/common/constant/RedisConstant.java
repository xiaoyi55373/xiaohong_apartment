package com.xiaohong.lease.common.constant;

/**
 * Redis常量定义
 * 
 * @author 小红
 */
public class RedisConstant {

    // ==================== 登录相关 ====================
    
    /**
     * APP端登录验证码Key前缀
     */
    public static final String APP_LOGIN_PREFIX = "app:login:";
    
    /**
     * APP端登录验证码重发时间（秒）
     */
    public static final Integer APP_LOGIN_CODE_RESEND_TIME_SEC = 60;
    
    /**
     * APP端登录验证码过期时间（秒）
     */
    public static final Integer APP_LOGIN_CODE_TTL_SEC = 60 * 10;
    
    /**
     * 管理端登录验证码Key前缀
     */
    public static final String ADMIN_LOGIN_PREFIX = "admin:login:";
    
    /**
     * 管理端登录验证码过期时间（秒）
     */
    public static final Integer ADMIN_LOGIN_CAPTCHA_TTL_SEC = 600;

    // ==================== 限流相关 ====================
    
    /**
     * 接口限流Key前缀
     */
    public static final String RATE_LIMIT_PREFIX = "rate:limit:";
    
    /**
     * 用户限流Key前缀
     */
    public static final String USER_RATE_LIMIT_PREFIX = "rate:user:";

    // ==================== 分布式锁相关 ====================
    
    /**
     * 分布式锁Key前缀
     */
    public static final String LOCK_PREFIX = "lock:";
    
    /**
     * 默认锁过期时间（秒）
     */
    public static final Long LOCK_DEFAULT_EXPIRE_SECONDS = 30L;

    // ==================== 统计相关 ====================
    
    /**
     * 用户行为统计Key前缀
     */
    public static final String BEHAVIOR_STATS_PREFIX = "stats:behavior:";
    
    /**
     * 接口访问统计Key前缀
     */
    public static final String API_STATS_PREFIX = "stats:api:";
    
    /**
     * 热门房源统计Key前缀
     */
    public static final String HOT_APARTMENT_PREFIX = "stats:hot:apartment:";
}
