# 小红公寓claw - SQL优化建议文档

> 本文档基于性能压测结果，提供SQL查询优化建议和索引优化方案。

## 📊 性能指标基准

| 指标 | 目标值 | 说明 |
|------|--------|------|
| P95响应时间 | ≤ 1000ms | 95%的请求响应时间 |
| P99响应时间 | ≤ 2000ms | 99%的请求响应时间 |
| 平均响应时间 | ≤ 500ms | 平均响应时间 |
| 错误率 | ≤ 1% | 请求失败率 |
| 吞吐量 | ≥ 100 req/s | 每秒处理请求数 |

---

## 🔍 慢查询分析

### 1. 公寓列表查询 (apartment_info)

**当前SQL分析：**
```sql
SELECT id, name, introduction, district_id, district_name, 
       city_id, city_name, province_id, province_name, 
       address_detail, latitude, longitude, phone, is_release,
       IFNULL(tc.cnt, 0) total_room_count,
       IFNULL(tc.cnt, 0) - IFNULL(cc.cnt, 0) free_room_count
FROM (
    SELECT id, name, introduction, district_id, district_name,
           city_id, city_name, province_id, province_name,
           address_detail, latitude, longitude, phone, is_release
    FROM apartment_info
    WHERE is_deleted = 0
      AND province_id = ?
      AND city_id = ?
      AND district_id = ?
) ai
LEFT JOIN (
    SELECT apartment_id, COUNT(*) cnt
    FROM room_info
    WHERE is_deleted = 0
      AND is_release = 1
    GROUP BY apartment_id
) tc ON ai.id = tc.apartment_id
LEFT JOIN (
    SELECT apartment_id, COUNT(*) cnt
    FROM lease_agreement
    WHERE is_deleted = 0
      AND status IN (2, 5)
    GROUP BY apartment_id
) cc ON ai.id = cc.apartment_id
```

**问题分析：**
1. 子查询 `tc` 和 `cc` 使用了 `GROUP BY`，每次查询都会全表扫描
2. 缺少合适的复合索引
3. 查询涉及3个表的JOIN操作

**优化建议：**

```sql
-- 1. 为 apartment_info 表添加复合索引
ALTER TABLE apartment_info 
ADD INDEX idx_apartment_query (is_deleted, province_id, city_id, district_id, is_release);

-- 2. 为 room_info 表添加索引
ALTER TABLE room_info 
ADD INDEX idx_room_apartment (apartment_id, is_deleted, is_release);

-- 3. 为 lease_agreement 表添加索引
ALTER TABLE lease_agreement 
ADD INDEX idx_lease_apartment (apartment_id, is_deleted, status);

-- 4. 创建统计表（推荐方案）
-- 对于房间数和租约数的统计，建议创建冗余字段或统计表
ALTER TABLE apartment_info 
ADD COLUMN total_room_count INT DEFAULT 0 COMMENT '房间总数',
ADD COLUMN occupied_room_count INT DEFAULT 0 COMMENT '已租房间数';

-- 创建触发器自动更新统计
DELIMITER //

CREATE TRIGGER trg_room_insert 
AFTER INSERT ON room_info
FOR EACH ROW
BEGIN
    IF NEW.is_deleted = 0 AND NEW.is_release = 1 THEN
        UPDATE apartment_info 
        SET total_room_count = total_room_count + 1
        WHERE id = NEW.apartment_id;
    END IF;
END//

CREATE TRIGGER trg_room_delete
AFTER UPDATE ON room_info
FOR EACH ROW
BEGIN
    IF OLD.is_deleted = 0 AND NEW.is_deleted = 1 THEN
        UPDATE apartment_info 
        SET total_room_count = total_room_count - 1
        WHERE id = NEW.apartment_id;
    END IF;
END//

CREATE TRIGGER trg_lease_insert
AFTER INSERT ON lease_agreement
FOR EACH ROW
BEGIN
    IF NEW.is_deleted = 0 AND NEW.status IN (2, 5) THEN
        UPDATE apartment_info 
        SET occupied_room_count = occupied_room_count + 1
        WHERE id = NEW.apartment_id;
    END IF;
END//

DELIMITER ;
```

---

### 2. 房间列表查询 (room_info)

**当前SQL分析：**
```sql
SELECT ri.id, room_number, rent, ri.is_release,
       la.lease_end_date,
       la.room_id IS NOT NULL is_check_in,
       ai.id apartment_id, ai.name, ai.introduction,
       ai.district_id, ai.district_name, ai.city_id, ai.city_name,
       ai.province_id, ai.province_name, ai.address_detail,
       ai.latitude, ai.longitude, ai.phone, ai.is_release apartment_is_release
FROM (
    SELECT id, room_number, rent, apartment_id, is_release
    FROM room_info
    WHERE is_deleted = 0
) ri
LEFT JOIN (
    SELECT room_id, lease_end_date
    FROM lease_agreement
    WHERE is_deleted = 0
      AND status IN (2, 5)
) la ON ri.id = la.room_id
LEFT JOIN (
    SELECT id, name, introduction, district_id, district_name,
           city_id, city_name, province_id, province_name,
           address_detail, latitude, longitude, phone, is_release
    FROM apartment_info
    WHERE is_deleted = 0
) ai ON ri.apartment_id = ai.id
WHERE ai.province_id = ?
  AND ai.city_id = ?
  AND ai.district_id = ?
  AND ri.apartment_id = ?
```

**问题分析：**
1. 子查询 `la` 没有使用索引
2. 多表JOIN导致性能下降
3. 返回字段过多

**优化建议：**

```sql
-- 1. 为 room_info 添加复合索引
ALTER TABLE room_info 
ADD INDEX idx_room_query (is_deleted, apartment_id, is_release);

-- 2. 为 lease_agreement 添加索引（如果还没有）
ALTER TABLE lease_agreement 
ADD INDEX idx_lease_room (room_id, is_deleted, status, lease_end_date);

-- 3. 优化后的查询（使用覆盖索引）
SELECT ri.id, ri.room_number, ri.rent, ri.is_release,
       la.lease_end_date, la.room_id IS NOT NULL is_check_in,
       ai.id apartment_id, ai.name, ai.district_name, ai.city_name
FROM room_info ri
LEFT JOIN lease_agreement la ON ri.id = la.room_id 
    AND la.is_deleted = 0 
    AND la.status IN (2, 5)
LEFT JOIN apartment_info ai ON ri.apartment_id = ai.id
WHERE ri.is_deleted = 0
  AND ri.apartment_id = ?
  AND ai.province_id = ?
  AND ai.city_id = ?
  AND ai.district_id = ?
```

---

### 3. 租约列表查询 (lease_agreement)

**当前SQL分析：**
```sql
SELECT * FROM lease_agreement 
WHERE is_deleted = 0
ORDER BY create_time DESC
LIMIT ?, ?
```

**优化建议：**

```sql
-- 1. 添加复合索引
ALTER TABLE lease_agreement 
ADD INDEX idx_lease_list (is_deleted, create_time);

-- 2. 避免使用 SELECT *，只查询需要的字段
SELECT id, phone, name, identification_number, apartment_id, room_id,
       lease_start_date, lease_end_date, rent, deposit, status
FROM lease_agreement 
WHERE is_deleted = 0
ORDER BY create_time DESC
LIMIT ?, ?
```

---

### 4. 预约列表查询 (view_appointment)

**优化建议：**

```sql
-- 添加复合索引
ALTER TABLE view_appointment 
ADD INDEX idx_appointment_list (is_deleted, appointment_status, create_time);

-- 如果经常按用户查询
ALTER TABLE view_appointment 
ADD INDEX idx_appointment_user (user_id, is_deleted, create_time);
```

---

### 5. 用户浏览历史查询 (browsing_history)

**优化建议：**

```sql
-- 添加复合索引
ALTER TABLE browsing_history 
ADD INDEX idx_history_user (user_id, is_deleted, browse_time DESC);

-- 定期清理历史数据（保留最近6个月）
-- 可以创建定时任务执行
DELETE FROM browsing_history 
WHERE browse_time < DATE_SUB(NOW(), INTERVAL 6 MONTH);
```

---

## 📈 索引优化清单

### 必须创建的索引

| 表名 | 索引名 | 索引字段 | 说明 |
|------|--------|----------|------|
| apartment_info | idx_apartment_query | (is_deleted, province_id, city_id, district_id) | 公寓列表查询 |
| apartment_info | idx_apartment_release | (is_deleted, is_release) | 发布状态查询 |
| room_info | idx_room_apartment | (apartment_id, is_deleted, is_release) | 房间统计查询 |
| room_info | idx_room_query | (is_deleted, apartment_id) | 房间列表查询 |
| lease_agreement | idx_lease_apartment | (apartment_id, is_deleted, status) | 租约统计查询 |
| lease_agreement | idx_lease_room | (room_id, is_deleted, status) | 房间租约查询 |
| lease_agreement | idx_lease_list | (is_deleted, create_time) | 租约列表查询 |
| view_appointment | idx_appointment_list | (is_deleted, appointment_status, create_time) | 预约列表查询 |
| view_appointment | idx_appointment_user | (user_id, is_deleted, create_time) | 用户预约查询 |
| browsing_history | idx_history_user | (user_id, is_deleted, browse_time) | 浏览历史查询 |
| system_user | idx_user_username | (username) | 用户名查询 |
| system_user | idx_user_status | (status, is_deleted) | 用户状态查询 |

---

## 🚀 查询优化技巧

### 1. 分页优化

对于大数据量的分页查询，使用覆盖索引优化：

```sql
-- 原查询
SELECT * FROM apartment_info 
WHERE is_deleted = 0
LIMIT 100000, 10;

-- 优化后
SELECT ai.* 
FROM apartment_info ai
INNER JOIN (
    SELECT id 
    FROM apartment_info 
    WHERE is_deleted = 0
    LIMIT 100000, 10
) tmp ON ai.id = tmp.id;
```

### 2. 避免全表扫描

```sql
-- 避免
SELECT * FROM room_info WHERE apartment_id IN (
    SELECT id FROM apartment_info WHERE city_id = 1
);

-- 优化
SELECT ri.* 
FROM room_info ri
INNER JOIN apartment_info ai ON ri.apartment_id = ai.id
WHERE ai.city_id = 1;
```

### 3. 使用 EXPLAIN 分析查询

```sql
EXPLAIN SELECT * FROM apartment_info 
WHERE is_deleted = 0 AND city_id = 1;

-- 关注以下字段：
-- type: 至少达到 range，最好是 ref 或 eq_ref
-- key: 是否使用了索引
-- rows: 扫描行数，越少越好
-- Extra: 避免出现 Using filesort 和 Using temporary
```

---

## 🔧 数据库配置优化

### MySQL配置优化建议

```ini
# my.cnf 优化配置

[mysqld]
# 缓冲池大小（建议设置为物理内存的50%-75%）
innodb_buffer_pool_size = 2G

# 日志文件大小
innodb_log_file_size = 512M
innodb_log_files_in_group = 3

# 连接数
max_connections = 500

# 查询缓存（MySQL 8.0已移除）
# query_cache_type = 1
# query_cache_size = 64M

# 临时表大小
tmp_table_size = 64M
max_heap_table_size = 64M

# 排序缓冲区
sort_buffer_size = 2M
read_buffer_size = 2M
read_rnd_buffer_size = 4M

# 连接缓冲区
join_buffer_size = 2M

# InnoDB刷新方式
innodb_flush_method = O_DIRECT
innodb_flush_log_at_trx_commit = 2

# 慢查询日志
slow_query_log = 1
slow_query_log_file = /var/log/mysql/slow.log
long_query_time = 1
```

---

## 📋 执行计划

### 第一阶段：索引优化（立即执行）

```sql
-- 执行以下索引创建语句
-- 注意：在业务低峰期执行，避免锁表

-- 1. 公寓相关索引
ALTER TABLE apartment_info 
ADD INDEX idx_apartment_query (is_deleted, province_id, city_id, district_id);

-- 2. 房间相关索引
ALTER TABLE room_info 
ADD INDEX idx_room_apartment (apartment_id, is_deleted, is_release);

-- 3. 租约相关索引
ALTER TABLE lease_agreement 
ADD INDEX idx_lease_apartment (apartment_id, is_deleted, status),
ADD INDEX idx_lease_list (is_deleted, create_time);

-- 4. 预约相关索引
ALTER TABLE view_appointment 
ADD INDEX idx_appointment_list (is_deleted, appointment_status, create_time);
```

### 第二阶段：应用层优化（1周内）

1. 修改Mapper XML，优化SQL语句
2. 添加数据库连接池监控
3. 实现查询结果缓存

### 第三阶段：架构优化（2周内）

1. 引入Redis缓存热点数据
2. 实现读写分离
3. 考虑分库分表（如果数据量超过1000万）

---

## 📊 监控指标

建议监控以下数据库性能指标：

| 指标 | 告警阈值 | 说明 |
|------|----------|------|
| 慢查询数量 | > 10/分钟 | 慢查询监控 |
| 查询响应时间 | P95 > 1000ms | 查询性能 |
| 连接数使用率 | > 80% | 连接池监控 |
| CPU使用率 | > 70% | 服务器负载 |
| 内存使用率 | > 80% | 内存监控 |
| 磁盘I/O | > 80% | IO性能 |

---

## 📝 注意事项

1. **索引创建时机**：建议在业务低峰期执行，大表创建索引可能需要较长时间
2. **索引维护**：定期使用 `OPTIMIZE TABLE` 维护表
3. **监控验证**：创建索引后需要通过 `EXPLAIN` 验证是否生效
4. **回滚方案**：保留原始SQL，以便出现问题时快速回滚

---

*文档版本: 1.0*  
*创建时间: 2026-04-03*  
*作者: 小红公寓claw开发团队*
