-- ========================================================
-- 小红公寓claw - 数据库性能优化脚本
-- 创建时间: 2026-04-03
-- 说明: 本脚本包含所有推荐的数据库索引和优化
-- 警告: 请在业务低峰期执行，大表创建索引可能需要较长时间
-- ========================================================

-- ========================================================
-- 第一部分: 索引创建
-- ========================================================

-- 1. apartment_info 表索引
-- 用于公寓列表查询
CREATE INDEX IF NOT EXISTS idx_apartment_query 
ON apartment_info (is_deleted, province_id, city_id, district_id);

-- 用于发布状态查询
CREATE INDEX IF NOT EXISTS idx_apartment_release 
ON apartment_info (is_deleted, is_release);

-- 用于按名称搜索
CREATE INDEX IF NOT EXISTS idx_apartment_name 
ON apartment_info (name);

-- 2. room_info 表索引
-- 用于房间统计查询
CREATE INDEX IF NOT EXISTS idx_room_apartment 
ON room_info (apartment_id, is_deleted, is_release);

-- 用于房间列表查询
CREATE INDEX IF NOT EXISTS idx_room_query 
ON room_info (is_deleted, apartment_id);

-- 3. lease_agreement 表索引
-- 用于租约统计查询
CREATE INDEX IF NOT EXISTS idx_lease_apartment 
ON lease_agreement (apartment_id, is_deleted, status);

-- 用于房间租约查询
CREATE INDEX IF NOT EXISTS idx_lease_room 
ON lease_agreement (room_id, is_deleted, status);

-- 用于租约列表查询
CREATE INDEX IF NOT EXISTS idx_lease_list 
ON lease_agreement (is_deleted, create_time);

-- 用于租约状态查询
CREATE INDEX IF NOT EXISTS idx_lease_status 
ON lease_agreement (status, is_deleted);

-- 4. view_appointment 表索引
-- 用于预约列表查询
CREATE INDEX IF NOT EXISTS idx_appointment_list 
ON view_appointment (is_deleted, appointment_status, create_time);

-- 用于用户预约查询
CREATE INDEX IF NOT EXISTS idx_appointment_user 
ON view_appointment (user_id, is_deleted, create_time);

-- 用于预约状态查询
CREATE INDEX IF NOT EXISTS idx_appointment_status 
ON view_appointment (appointment_status, is_deleted);

-- 5. browsing_history 表索引
-- 用于浏览历史查询
CREATE INDEX IF NOT EXISTS idx_history_user 
ON browsing_history (user_id, is_deleted, browse_time);

-- 用于按房源查询浏览记录
CREATE INDEX IF NOT EXISTS idx_history_item 
ON browsing_history (item_id, item_type, is_deleted);

-- 6. system_user 表索引
-- 用于用户名查询
CREATE INDEX IF NOT EXISTS idx_user_username 
ON system_user (username);

-- 用于用户状态查询
CREATE INDEX IF NOT EXISTS idx_user_status 
ON system_user (status, is_deleted);

-- 7. graph_info 表索引
-- 用于图片查询
CREATE INDEX IF NOT EXISTS idx_graph_item 
ON graph_info (item_id, item_type, is_deleted);

-- 8. facility_info 表索引
-- 用于配套查询
CREATE INDEX IF NOT EXISTS idx_facility_type 
ON facility_info (type, is_deleted);

-- 9. label_info 表索引
-- 用于标签查询
CREATE INDEX IF NOT EXISTS idx_label_type 
ON label_info (type, is_deleted);

-- 10. attr_key 表索引
CREATE INDEX IF NOT EXISTS idx_attr_key_deleted 
ON attr_key (is_deleted);

-- 11. attr_value 索引
CREATE INDEX IF NOT EXISTS idx_attr_value_key 
ON attr_value (attr_key_id, is_deleted);

-- 12. fee_key 索引
CREATE INDEX IF NOT EXISTS idx_fee_key_deleted 
ON fee_key (is_deleted);

-- 13. fee_value 索引
CREATE INDEX IF NOT EXISTS idx_fee_value_key 
ON fee_value (fee_key_id, is_deleted);

-- 14. payment_type 索引
CREATE INDEX IF NOT EXISTS idx_payment_deleted 
ON payment_type (is_deleted);

-- 15. lease_term 索引
CREATE INDEX IF NOT EXISTS idx_term_deleted 
ON lease_term (is_deleted);

-- ========================================================
-- 第二部分: 统计表和触发器（可选）
-- 说明: 如果房间数和租约数统计成为瓶颈，可以使用以下方案
-- ========================================================

-- 创建公寓统计表
CREATE TABLE IF NOT EXISTS apartment_statistics (
    apartment_id BIGINT PRIMARY KEY COMMENT '公寓ID',
    total_room_count INT DEFAULT 0 COMMENT '房间总数',
    released_room_count INT DEFAULT 0 COMMENT '已发布房间数',
    occupied_room_count INT DEFAULT 0 COMMENT '已租房间数',
    available_room_count INT DEFAULT 0 COMMENT '可租房源数',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (apartment_id) REFERENCES apartment_info(id)
) COMMENT='公寓统计表';

-- 初始化统计数据
INSERT INTO apartment_statistics (apartment_id, total_room_count, released_room_count)
SELECT 
    ai.id,
    COUNT(ri.id) as total_room_count,
    SUM(CASE WHEN ri.is_release = 1 AND ri.is_deleted = 0 THEN 1 ELSE 0 END) as released_room_count
FROM apartment_info ai
LEFT JOIN room_info ri ON ai.id = ri.apartment_id AND ri.is_deleted = 0
WHERE ai.is_deleted = 0
GROUP BY ai.id
ON DUPLICATE KEY UPDATE
    total_room_count = VALUES(total_room_count),
    released_room_count = VALUES(released_room_count);

-- 更新已租房间数
UPDATE apartment_statistics ast
SET occupied_room_count = (
    SELECT COUNT(*)
    FROM lease_agreement la
    WHERE la.apartment_id = ast.apartment_id
      AND la.is_deleted = 0
      AND la.status IN (2, 5)  -- SIGNED, WITHDRAWING
);

-- 更新可租房源数
UPDATE apartment_statistics
SET available_room_count = released_room_count - occupied_room_count;

-- ========================================================
-- 第三部分: 慢查询日志配置
-- ========================================================

-- 开启慢查询日志（需要SUPER权限）
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 1;  -- 超过1秒的查询记录
SET GLOBAL log_queries_not_using_indexes = 'ON';  -- 记录未使用索引的查询

-- 查看慢查询日志状态
SHOW VARIABLES LIKE 'slow_query%';
SHOW VARIABLES LIKE 'long_query%';

-- ========================================================
-- 第四部分: 查询优化提示
-- ========================================================

-- 1. 查看表统计信息
-- ANALYZE TABLE apartment_info;
-- ANALYZE TABLE room_info;
-- ANALYZE TABLE lease_agreement;

-- 2. 优化表（整理碎片）
-- OPTIMIZE TABLE apartment_info;
-- OPTIMIZE TABLE room_info;
-- OPTIMIZE TABLE lease_agreement;

-- 3. 查看索引使用情况
-- SELECT 
--     TABLE_NAME,
--     INDEX_NAME,
--     CARDINALITY
-- FROM information_schema.STATISTICS
-- WHERE TABLE_SCHEMA = DATABASE()
-- ORDER BY TABLE_NAME, INDEX_NAME;

-- ========================================================
-- 第五部分: 常用性能查询
-- ========================================================

-- 查看当前连接数
-- SHOW STATUS LIKE 'Threads_connected';
-- SHOW STATUS LIKE 'Max_used_connections';

-- 查看查询缓存状态（MySQL 8.0已移除）
-- SHOW STATUS LIKE 'Qcache%';

-- 查看InnoDB状态
-- SHOW ENGINE INNODB STATUS;

-- 查看锁等待
-- SELECT * FROM information_schema.INNODB_LOCK_WAITS;
-- SELECT * FROM information_schema.INNODB_LOCKS;

-- 查看正在执行的查询
-- SHOW FULL PROCESSLIST;

-- 查看表大小
-- SELECT 
--     table_name,
--     ROUND(data_length / 1024 / 1024, 2) AS data_size_mb,
--     ROUND(index_length / 1024 / 1024, 2) AS index_size_mb,
--     ROUND((data_length + index_length) / 1024 / 1024, 2) AS total_size_mb
-- FROM information_schema.TABLES
-- WHERE table_schema = DATABASE()
-- ORDER BY total_size_mb DESC;

-- ========================================================
-- 第六部分: 清理脚本（谨慎使用）
-- ========================================================

-- 清理6个月前的浏览历史
-- DELETE FROM browsing_history 
-- WHERE browse_time < DATE_SUB(NOW(), INTERVAL 6 MONTH);

-- 清理已删除的数据（物理删除）
-- DELETE FROM apartment_info WHERE is_deleted = 1 AND update_time < DATE_SUB(NOW(), INTERVAL 1 YEAR);
-- DELETE FROM room_info WHERE is_deleted = 1 AND update_time < DATE_SUB(NOW(), INTERVAL 1 YEAR);

-- ========================================================
-- 执行完成提示
-- ========================================================

SELECT '数据库性能优化脚本执行完成！' AS message;
SELECT '请检查索引是否正确创建' AS reminder;
SELECT '建议在业务低峰期执行大表索引创建' AS warning;
