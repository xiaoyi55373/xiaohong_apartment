# 小红公寓claw - Redis缓存优化指南

## 📋 概述

本文档描述了小红公寓claw项目的Redis缓存优化策略，包括缓存架构、配置说明、使用指南和最佳实践。

## 🏗️ 缓存架构

### 1. 缓存分层

```
┌─────────────────────────────────────────────────────────────┐
│                      缓存分层架构                            │
├─────────────────────────────────────────────────────────────┤
│  L1: 本地缓存 (Caffeine) - 热点数据                          │
│     └── 首页推荐、热门公寓等高频访问数据                      │
├─────────────────────────────────────────────────────────────┤
│  L2: 分布式缓存 (Redis) - 主要缓存层                         │
│     └── 公寓详情、房间信息、用户数据等                        │
├─────────────────────────────────────────────────────────────┤
│  L3: 数据库 (MySQL) - 持久化存储                             │
│     └── 核心业务数据                                         │
└─────────────────────────────────────────────────────────────┘
```

### 2. 缓存Key命名规范

```
xiaohong:{业务模块}:{数据类型}:{标识}

示例：
- xiaohong:apartment:detail:1001      # 公寓详情
- xiaohong:region:provinces           # 省份列表
- xiaohong:config:paymentTypes        # 支付方式列表
```

## ⚙️ 配置说明

### 1. 基础配置 (application.yml)

```yaml
spring:
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      database: 0
      # 连接池配置
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
          max-wait: 1000ms
      # 超时配置
      timeout: 5000ms

# 小红公寓claw 缓存配置
xiaohong:
  cache:
    enabled: true
  performance:
    enabled: true
```

### 2. 缓存区域配置

| 缓存区域 | 过期时间 | 说明 |
|---------|---------|------|
| apartment | 30分钟 | 公寓相关数据 |
| room | 30分钟 | 房间相关数据 |
| region | 1小时 | 地区数据（变化少） |
| config | 2小时 | 配置数据（几乎不变） |
| user | 30分钟 | 用户相关数据 |
| agreement | 30分钟 | 租约相关数据 |
| appointment | 20分钟 | 预约相关数据 |
| system | 1小时 | 系统相关数据 |
| hot | 10分钟 | 热点数据 |

## 📦 核心组件

### 1. CacheConstant - 缓存常量

定义所有缓存Key前缀和过期时间：

```java
// 公寓详情缓存
public static final String APARTMENT_DETAIL_KEY = "xiaohong:apartment:detail";
public static final Long APARTMENT_DETAIL_EXPIRE = 30L;

// 构建缓存Key
String key = CacheConstant.buildKey(CacheConstant.APARTMENT_DETAIL_KEY, apartmentId);
```

### 2. CacheService - 缓存服务

提供统一的缓存操作方法：

```java
@Autowired
private CacheService cacheService;

// 获取缓存
ApartmentDetailVo detail = cacheService.get(key);

// 设置缓存（带过期时间）
cacheService.set(key, detail, 30);

// 设置缓存（带随机过期时间，防止缓存雪崩）
cacheService.setWithRandomExpire(key, detail, 30, 10);

// 获取或加载（带缓存穿透防护）
ApartmentDetailVo detail = cacheService.getOrLoad(key, 30, () -> {
    return loadFromDatabase(id);
});

// 根据模式删除缓存
cacheService.deleteByPattern("xiaohong:apartment:*");
```

### 3. 注解式缓存

使用Spring Cache注解：

```java
// 查询缓存，不存在则执行方法
@Cacheable(value = "apartment", key = CacheConstant.APARTMENT_DETAIL_KEY + ":#id")
public ApartmentDetailVo getDetailById(Long id) {
    // 查询数据库
}

// 更新数据时清除缓存
@CacheEvict(value = "apartment", key = CacheConstant.APARTMENT_DETAIL_KEY + ":#entity.id")
public boolean updateById(ApartmentInfo entity) {
    // 更新数据库
}

// 批量清除缓存
@CacheEvict(value = "apartment", allEntries = true)
public boolean save(ApartmentInfo entity) {
    // 保存数据
}
```

## 🔒 缓存安全策略

### 1. 缓存穿透防护

```java
// CacheService自动处理空值缓存
public <T> T getOrLoad(String key, long minutes, Supplier<T> loader) {
    T value = get(key);
    if (value != null) {
        return value;
    }
    
    // 检查是否是空值标记
    Object cachedValue = redisTemplate.opsForValue().get(key);
    if (NULL_VALUE.equals(cachedValue)) {
        return null;  // 数据库中不存在，直接返回null
    }
    
    // 从数据库加载
    value = loader.get();
    set(key, value, minutes);  // 空值也会缓存
    return value;
}
```

### 2. 缓存雪崩防护

```java
// 使用随机过期时间
public void setWithRandomExpire(String key, Object value, long minutes, long randomMinutes) {
    long expireMinutes = minutes + (long) (Math.random() * randomMinutes);
    set(key, value, expireMinutes);
}
```

### 3. 缓存击穿防护

使用分布式锁（待实现）：

```java
@Cacheable(value = "apartment", key = "#id", sync = true)
public ApartmentDetailVo getDetailById(Long id) {
    // sync = true 防止缓存击穿
}
```

## 📊 缓存监控

### 1. 监控接口

```
GET /admin/monitor/cache/stats          # 获取缓存统计
GET /admin/monitor/cache/keys           # 获取缓存Key列表
GET /admin/monitor/cache/detail?key=xxx # 获取缓存详情
DELETE /admin/monitor/cache/clear       # 清空所有缓存
DELETE /admin/monitor/cache/delete?key=xxx # 删除指定缓存
```

### 2. 统计信息示例

```json
{
  "totalKeys": 156,
  "typeDistribution": {
    "apartment": 45,
    "room": 32,
    "region": 12,
    "config": 8,
    "user": 28,
    "agreement": 15,
    "appointment": 16
  }
}
```

## ✅ 最佳实践

### 1. 缓存使用原则

1. **读多写少的数据适合缓存**
   - 公寓详情、房间信息
   - 地区数据、配置数据
   - 用户信息

2. **实时性要求高的数据不适合缓存**
   - 订单状态
   - 库存数量
   - 实时统计数据

3. **大对象谨慎缓存**
   - 分页列表数据建议只缓存前几页
   - 大图片使用对象存储URL

### 2. 缓存更新策略

| 策略 | 适用场景 | 实现方式 |
|-----|---------|---------|
| Cache-Aside | 读多写少 | 先读缓存，未命中读数据库 |
| Write-Through | 数据一致性要求高 | 写缓存同时写数据库 |
| Write-Behind | 写性能要求高 | 先写缓存，异步写数据库 |

### 3. 缓存Key设计

```java
// ✅ 好的设计：包含版本号，便于批量更新
String key = "xiaohong:apartment:detail:v1:" + id;

// ✅ 好的设计：使用有意义的命名
String key = CacheConstant.buildKey(CacheConstant.APARTMENT_DETAIL_KEY, id);

// ❌ 避免：使用无意义的Key
String key = "data_" + id;

// ❌ 避免：Key过长
String key = "xiaohong:apartment:detail:room:list:facility:" + id;
```

### 4. 缓存过期时间设置

```java
// 热点数据：短过期时间，保持新鲜
public static final Long HOT_APARTMENTS_EXPIRE = 5L;

// 基础数据：中等过期时间
public static final Long APARTMENT_DETAIL_EXPIRE = 30L;

// 配置数据：长过期时间
public static final Long PAYMENT_TYPES_EXPIRE = 120L;

// 用户数据：根据业务需求
public static final Long USER_INFO_EXPIRE = 30L;
```

## 🔧 故障处理

### 1. Redis连接失败

```java
// 使用try-catch包装缓存操作
try {
    value = cacheService.get(key);
} catch (Exception e) {
    log.error("缓存获取失败，回退到数据库", e);
    value = loadFromDatabase(id);
}
```

### 2. 缓存雪崩

- 设置随机过期时间
- 使用熔断器（Hystrix/Resilience4j）
- 多级缓存架构

### 3. 缓存穿透

- 缓存空值
- 布隆过滤器（待实现）
- 参数校验

## 📈 性能优化建议

1. **连接池优化**
   - 根据并发量调整max-active
   - 监控连接池使用率

2. **序列化优化**
   - 使用JSON序列化（已配置）
   - 避免存储大对象

3. **Pipeline批量操作**
   - 批量读取/写入时使用Pipeline

4. **本地缓存**
   - 热点数据使用Caffeine本地缓存
   - 减少网络开销

## 📝 更新日志

| 日期 | 版本 | 说明 |
|-----|-----|------|
| 2026-04-03 | 1.0 | 初始版本，实现基础缓存架构 |

---

*本文档由小红公寓claw开发团队维护*
