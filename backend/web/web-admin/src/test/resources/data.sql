-- 小红公寓claw API 测试数据初始化脚本
-- 创建基础测试数据

-- ==================== 系统管理数据 ====================

-- 系统岗位数据
INSERT INTO system_post (id, name, code, status, create_time, update_time, is_deleted) VALUES
(1, '超级管理员', 'SUPER_ADMIN', 1, NOW(), NOW(), 0),
(2, '管理员', 'ADMIN', 1, NOW(), NOW(), 0),
(3, '运营人员', 'OPERATOR', 1, NOW(), NOW(), 0),
(4, '客服人员', 'CUSTOMER_SERVICE', 1, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE id = id;

-- 系统用户数据 (密码: admin)
INSERT INTO system_user (id, username, password, name, type, phone, avatar_url, additional_info, post_id, status, create_time, update_time, is_deleted) VALUES
(1, 'admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '系统管理员', 0, '13800138000', 'https://example.com/avatar1.png', '超级管理员账号', 1, 1, NOW(), NOW(), 0),
(2, 'operator', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '运营人员', 1, '13800138001', 'https://example.com/avatar2.png', '运营人员账号', 3, 1, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE id = id;

-- ==================== 区域数据 ====================

-- 省份数据
INSERT INTO province_info (id, name) VALUES
(1, '北京市'),
(2, '上海市'),
(3, '广东省'),
(4, '浙江省'),
(5, '江苏省')
ON DUPLICATE KEY UPDATE id = id;

-- 城市数据
INSERT INTO city_info (id, name, province_id) VALUES
(1, '北京市', 1),
(2, '上海市', 2),
(3, '广州市', 3),
(4, '深圳市', 3),
(5, '杭州市', 4),
(6, '南京市', 5),
(7, '苏州市', 5)
ON DUPLICATE KEY UPDATE id = id;

-- 区县数据
INSERT INTO district_info (id, name, city_id) VALUES
(1, '朝阳区', 1),
(2, '海淀区', 1),
(3, '浦东新区', 2),
(4, '徐汇区', 2),
(5, '天河区', 3),
(6, '越秀区', 3),
(7, '福田区', 4),
(8, '南山区', 4),
(9, '西湖区', 5),
(10, '上城区', 5),
(11, '鼓楼区', 6),
(12, '玄武区', 6),
(13, '工业园区', 7),
(14, '姑苏区', 7)
ON DUPLICATE KEY UPDATE id = id;

-- ==================== 用户信息数据 ====================

-- 用户信息 (密码: 123456)
INSERT INTO user_info (id, phone, password, status, nickname, avatar_url, create_time, update_time, is_deleted) VALUES
(1, '13800138001', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, '张三', 'https://example.com/user1.png', NOW(), NOW(), 0),
(2, '13800138002', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, '李四', 'https://example.com/user2.png', NOW(), NOW(), 0),
(3, '13800138003', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, '王五', 'https://example.com/user3.png', NOW(), NOW(), 0),
(4, '13800138004', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 0, '赵六', 'https://example.com/user4.png', NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE id = id;

-- ==================== 公寓基础数据 ====================

-- 属性数据
INSERT INTO attr_key (id, name, create_time, update_time, is_deleted) VALUES
(1, '户型', NOW(), NOW(), 0),
(2, '面积', NOW(), NOW(), 0),
(3, '朝向', NOW(), NOW(), 0),
(4, '楼层', NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE id = id;

INSERT INTO attr_value (id, name, attr_key_id, create_time, update_time, is_deleted) VALUES
(1, '一室一厅', 1, NOW(), NOW(), 0),
(2, '两室一厅', 1, NOW(), NOW(), 0),
(3, '三室两厅', 1, NOW(), NOW(), 0),
(4, '50㎡以下', 2, NOW(), NOW(), 0),
(5, '50-80㎡', 2, NOW(), NOW(), 0),
(6, '80-100㎡', 2, NOW(), NOW(), 0),
(7, '朝南', 3, NOW(), NOW(), 0),
(8, '朝北', 3, NOW(), NOW(), 0),
(9, '朝东', 3, NOW(), NOW(), 0),
(10, '朝西', 3, NOW(), NOW(), 0),
(11, '低楼层', 4, NOW(), NOW(), 0),
(12, '中楼层', 4, NOW(), NOW(), 0),
(13, '高楼层', 4, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE id = id;

-- 杂费数据
INSERT INTO fee_key (id, name, create_time, update_time, is_deleted) VALUES
(1, '水费', NOW(), NOW(), 0),
(2, '电费', NOW(), NOW(), 0),
(3, '燃气费', NOW(), NOW(), 0),
(4, '物业费', NOW(), NOW(), 0),
(5, '网费', NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE id = id;

INSERT INTO fee_value (id, name, unit, fee_key_id, create_time, update_time, is_deleted) VALUES
(1, '居民用水', '元/吨', 1, NOW(), NOW(), 0),
(2, '商业用水', '元/吨', 1, NOW(), NOW(), 0),
(3, '居民用电', '元/度', 2, NOW(), NOW(), 0),
(4, '商业用电', '元/度', 2, NOW(), NOW(), 0),
(5, '居民燃气', '元/立方', 3, NOW(), NOW(), 0),
(6, '标准物业', '元/㎡·月', 4, NOW(), NOW(), 0),
(7, '百兆宽带', '元/月', 5, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE id = id;

-- 支付方式
INSERT INTO payment_type (id, name, pay_month_count, additional_info, create_time, update_time, is_deleted) VALUES
(1, '月付', 1, '每月支付租金', NOW(), NOW(), 0),
(2, '季付', 3, '每3个月支付租金', NOW(), NOW(), 0),
(3, '半年付', 6, '每6个月支付租金', NOW(), NOW(), 0),
(4, '年付', 12, '每年支付租金', NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE id = id;

-- 租期
INSERT INTO lease_term (id, unit, unit_count, create_time, update_time, is_deleted) VALUES
(1, 1, 3, NOW(), NOW(), 0),
(2, 1, 6, NOW(), NOW(), 0),
(3, 1, 12, NOW(), NOW(), 0),
(4, 1, 24, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE id = id;

-- 配套设施
INSERT INTO facility_info (id, type, name, create_time, update_time, is_deleted) VALUES
(1, 1, '洗衣机', NOW(), NOW(), 0),
(2, 1, '冰箱', NOW(), NOW(), 0),
(3, 1, '空调', NOW(), NOW(), 0),
(4, 1, '电视', NOW(), NOW(), 0),
(5, 1, '热水器', NOW(), NOW(), 0),
(6, 1, '宽带', NOW(), NOW(), 0),
(7, 1, '沙发', NOW(), NOW(), 0),
(8, 1, '床', NOW(), NOW(), 0),
(9, 1, '衣柜', NOW(), NOW(), 0),
(10, 1, '书桌', NOW(), NOW(), 0),
(11, 2, '电梯', NOW(), NOW(), 0),
(12, 2, '停车位', NOW(), NOW(), 0),
(13, 2, '健身房', NOW(), NOW(), 0),
(14, 2, '游泳池', NOW(), NOW(), 0),
(15, 2, '花园', NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE id = id;

-- 标签
INSERT INTO label_info (id, type, name, create_time, update_time, is_deleted) VALUES
(1, 1, '近地铁', NOW(), NOW(), 0),
(2, 1, '精装修', NOW(), NOW(), 0),
(3, 1, '拎包入住', NOW(), NOW(), 0),
(4, 1, '南北通透', NOW(), NOW(), 0),
(5, 1, '采光好', NOW(), NOW(), 0),
(6, 2, '押一付一', NOW(), NOW(), 0),
(7, 2, '无中介费', NOW(), NOW(), 0),
(8, 2, '随时看房', NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE id = id;

-- ==================== 公寓数据 ====================

-- 公寓信息
INSERT INTO apartment_info (id, name, introduction, district_id, district_name, city_id, city_name, province_id, province_name, address_detail, latitude, longitude, phone, is_release, status, create_time, update_time, is_deleted) VALUES
(1, '小红公寓·阳光社区', '位于市中心，交通便利，周边配套设施齐全', 1, '朝阳区', 1, '北京市', 1, '北京市', '朝阳区建国路88号', '39.9042', '116.4074', '400-888-0001', 1, 0, NOW(), NOW(), 0),
(2, '小红公寓·绿地社区', '环境优美，绿化率高，适合居住', 3, '浦东新区', 2, '上海市', 2, '上海市', '浦东新区陆家嘴环路1000号', '31.2304', '121.4737', '400-888-0002', 1, 0, NOW(), NOW(), 0),
(3, '小红公寓·海景社区', '靠近海边，风景优美，空气清新', 7, '福田区', 4, '深圳市', 3, '广东省', '福田区福华路168号', '22.5431', '114.0579', '400-888-0003', 1, 0, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE id = id;

-- 房间信息
INSERT INTO room_info (id, apartment_id, room_number, rent, is_release, status, create_time, update_time, is_deleted) VALUES
(1, 1, '101', 3500.00, 1, 0, NOW(), NOW(), 0),
(2, 1, '102', 3800.00, 1, 0, NOW(), NOW(), 0),
(3, 1, '201', 4200.00, 1, 0, NOW(), NOW(), 0),
(4, 2, 'A101', 4500.00, 1, 0, NOW(), NOW(), 0),
(5, 2, 'A102', 4800.00, 1, 0, NOW(), NOW(), 0),
(6, 3, '1-101', 5000.00, 1, 0, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE id = id;

-- ==================== 预约看房数据 ====================

INSERT INTO view_appointment (id, user_id, apartment_id, appointment_time, appointment_status, create_time, update_time, is_deleted) VALUES
(1, 1, 1, DATE_ADD(NOW(), INTERVAL 1 DAY), 0, NOW(), NOW(), 0),
(2, 2, 2, DATE_ADD(NOW(), INTERVAL 2 DAY), 0, NOW(), NOW(), 0),
(3, 3, 1, DATE_ADD(NOW(), INTERVAL 3 DAY), 1, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE id = id;

-- ==================== 租约数据 ====================

INSERT INTO lease_agreement (id, user_id, apartment_id, room_id, lease_start_date, lease_end_date, rent, deposit, status, create_time, update_time, is_deleted) VALUES
(1, 1, 1, 1, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_ADD(NOW(), INTERVAL 11 MONTH), 3500.00, 3500.00, 0, NOW(), NOW(), 0),
(2, 2, 2, 4, DATE_SUB(NOW(), INTERVAL 2 MONTH), DATE_ADD(NOW(), INTERVAL 10 MONTH), 4500.00, 4500.00, 0, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE id = id;
