package com.xiaohong.lease.common.cache;

/**
 * 缓存常量定义
 * 定义所有缓存的key前缀和过期时间
 * 
 * @author 小红
 */
public class CacheConstant {

    /**
     * 缓存Key分隔符
     */
    public static final String CACHE_KEY_SEPARATOR = ":";

    /**
     * 缓存Key前缀
     */
    public static final String CACHE_KEY_PREFIX = "xiaohong";

    // ==================== 公寓相关缓存 ====================
    
    /**
     * 公寓详情缓存
     * key: apartment:detail:{apartmentId}
     * expire: 30分钟
     */
    public static final String APARTMENT_DETAIL_KEY = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "apartment" + CACHE_KEY_SEPARATOR + "detail";
    public static final Long APARTMENT_DETAIL_EXPIRE = 30L;

    /**
     * 公寓列表缓存
     * key: apartment:list:{page}:{size}:{conditionHash}
     * expire: 10分钟
     */
    public static final String APARTMENT_LIST_KEY = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "apartment" + CACHE_KEY_SEPARATOR + "list";
    public static final Long APARTMENT_LIST_EXPIRE = 10L;

    /**
     * 公寓最小租金缓存
     * key: apartment:minRent:{apartmentId}
     * expire: 30分钟
     */
    public static final String APARTMENT_MIN_RENT_KEY = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "apartment" + CACHE_KEY_SEPARATOR + "minRent";
    public static final Long APARTMENT_MIN_RENT_EXPIRE = 30L;

    // ==================== 房间相关缓存 ====================

    /**
     * 房间详情缓存
     * key: room:detail:{roomId}
     * expire: 30分钟
     */
    public static final String ROOM_DETAIL_KEY = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "room" + CACHE_KEY_SEPARATOR + "detail";
    public static final Long ROOM_DETAIL_EXPIRE = 30L;

    /**
     * 房间列表缓存（按公寓ID）
     * key: room:list:{apartmentId}
     * expire: 20分钟
     */
    public static final String ROOM_LIST_KEY = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "room" + CACHE_KEY_SEPARATOR + "list";
    public static final Long ROOM_LIST_EXPIRE = 20L;

    // ==================== 字典/配置相关缓存 ====================

    /**
     * 省份列表缓存
     * key: region:provinces
     * expire: 1小时
     */
    public static final String REGION_PROVINCES_KEY = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "region" + CACHE_KEY_SEPARATOR + "provinces";
    public static final Long REGION_PROVINCES_EXPIRE = 60L;

    /**
     * 城市列表缓存（按省份ID）
     * key: region:cities:{provinceId}
     * expire: 1小时
     */
    public static final String REGION_CITIES_KEY = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "region" + CACHE_KEY_SEPARATOR + "cities";
    public static final Long REGION_CITIES_EXPIRE = 60L;

    /**
     * 区县列表缓存（按城市ID）
     * key: region:districts:{cityId}
     * expire: 1小时
     */
    public static final String REGION_DISTRICTS_KEY = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "region" + CACHE_KEY_SEPARATOR + "districts";
    public static final Long REGION_DISTRICTS_EXPIRE = 60L;

    /**
     * 支付方式列表缓存
     * key: config:paymentTypes
     * expire: 2小时
     */
    public static final String PAYMENT_TYPES_KEY = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "config" + CACHE_KEY_SEPARATOR + "paymentTypes";
    public static final Long PAYMENT_TYPES_EXPIRE = 120L;

    /**
     * 租期列表缓存
     * key: config:leaseTerms
     * expire: 2小时
     */
    public static final String LEASE_TERMS_KEY = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "config" + CACHE_KEY_SEPARATOR + "leaseTerms";
    public static final Long LEASE_TERMS_EXPIRE = 120L;

    /**
     * 配套列表缓存
     * key: config:facilities
     * expire: 2小时
     */
    public static final String FACILITIES_KEY = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "config" + CACHE_KEY_SEPARATOR + "facilities";
    public static final Long FACILITIES_EXPIRE = 120L;

    /**
     * 标签列表缓存
     * key: config:labels
     * expire: 2小时
     */
    public static final String LABELS_KEY = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "config" + CACHE_KEY_SEPARATOR + "labels";
    public static final Long LABELS_EXPIRE = 120L;

    /**
     * 属性key列表缓存
     * key: config:attrKeys
     * expire: 2小时
     */
    public static final String ATTR_KEYS_KEY = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "config" + CACHE_KEY_SEPARATOR + "attrKeys";
    public static final Long ATTR_KEYS_EXPIRE = 120L;

    // ==================== 用户相关缓存 ====================

    /**
     * 用户信息缓存
     * key: user:info:{userId}
     * expire: 30分钟
     */
    public static final String USER_INFO_KEY = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "user" + CACHE_KEY_SEPARATOR + "info";
    public static final Long USER_INFO_EXPIRE = 30L;

    /**
     * 用户浏览历史缓存
     * key: user:history:{userId}
     * expire: 7天
     */
    public static final String USER_HISTORY_KEY = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "user" + CACHE_KEY_SEPARATOR + "history";
    public static final Long USER_HISTORY_EXPIRE = 7 * 24 * 60L;

    // ==================== 租约相关缓存 ====================

    /**
     * 租约详情缓存
     * key: agreement:detail:{agreementId}
     * expire: 30分钟
     */
    public static final String AGREEMENT_DETAIL_KEY = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "agreement" + CACHE_KEY_SEPARATOR + "detail";
    public static final Long AGREEMENT_DETAIL_EXPIRE = 30L;

    /**
     * 用户租约列表缓存
     * key: agreement:user:{userId}
     * expire: 15分钟
     */
    public static final String AGREEMENT_USER_LIST_KEY = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "agreement" + CACHE_KEY_SEPARATOR + "user";
    public static final Long AGREEMENT_USER_LIST_EXPIRE = 15L;

    // ==================== 预约相关缓存 ====================

    /**
     * 预约详情缓存
     * key: appointment:detail:{appointmentId}
     * expire: 20分钟
     */
    public static final String APPOINTMENT_DETAIL_KEY = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "appointment" + CACHE_KEY_SEPARATOR + "detail";
    public static final Long APPOINTMENT_DETAIL_EXPIRE = 20L;

    /**
     * 用户预约列表缓存
     * key: appointment:user:{userId}
     * expire: 15分钟
     */
    public static final String APPOINTMENT_USER_LIST_KEY = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "appointment" + CACHE_KEY_SEPARATOR + "user";
    public static final Long APPOINTMENT_USER_LIST_EXPIRE = 15L;

    // ==================== 系统相关缓存 ====================

    /**
     * 系统用户缓存
     * key: system:user:{userId}
     * expire: 30分钟
     */
    public static final String SYSTEM_USER_KEY = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "system" + CACHE_KEY_SEPARATOR + "user";
    public static final Long SYSTEM_USER_EXPIRE = 30L;

    /**
     * 系统菜单缓存
     * key: system:menus:{userId}
     * expire: 1小时
     */
    public static final String SYSTEM_MENUS_KEY = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "system" + CACHE_KEY_SEPARATOR + "menus";
    public static final Long SYSTEM_MENUS_EXPIRE = 60L;

    // ==================== 热点数据缓存（使用本地缓存Caffeine） ====================

    /**
     * 热点公寓列表（本地缓存）
     * expire: 5分钟
     */
    public static final String HOT_APARTMENTS_KEY = "hot:apartments";
    public static final Long HOT_APARTMENTS_EXPIRE = 5L;

    /**
     * 首页推荐数据（本地缓存）
     * expire: 10分钟
     */
    public static final String HOME_RECOMMEND_KEY = "home:recommend";
    public static final Long HOME_RECOMMEND_EXPIRE = 10L;

    // ==================== 智能推荐相关缓存 ====================

    /**
     * 用户个性化推荐缓存
     * key: recommend:user:{userId}:{type}:{limit}
     * expire: 30分钟
     */
    public static final String RECOMMENDATION_USER = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "recommend" + CACHE_KEY_SEPARATOR + "user";
    public static final Long RECOMMENDATION_EXPIRE = 30L;

    /**
     * 用户偏好缓存
     * key: recommend:pref:{userId}
     * expire: 1小时
     */
    public static final String RECOMMENDATION_USER_PREF = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "recommend" + CACHE_KEY_SEPARATOR + "pref";
    public static final Long RECOMMENDATION_USER_PREF_EXPIRE = 60L;

    /**
     * 热门推荐缓存
     * key: recommend:hot:{limit}
     * expire: 20分钟
     */
    public static final String RECOMMENDATION_HOT = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "recommend" + CACHE_KEY_SEPARATOR + "hot";
    public static final Long RECOMMENDATION_HOT_EXPIRE = 20L;

    /**
     * 相似房源推荐缓存
     * key: recommend:similar:{roomId}:{limit}
     * expire: 30分钟
     */
    public static final String RECOMMENDATION_SIMILAR = CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "recommend" + CACHE_KEY_SEPARATOR + "similar";
    public static final Long RECOMMENDATION_SIMILAR_EXPIRE = 30L;

    /**
     * 构建缓存Key
     * 
     * @param prefix 前缀
     * @param params 参数
     * @return 完整的缓存Key
     */
    public static String buildKey(String prefix, Object... params) {
        StringBuilder key = new StringBuilder(prefix);
        for (Object param : params) {
            key.append(CACHE_KEY_SEPARATOR).append(param);
        }
        return key.toString();
    }
}
