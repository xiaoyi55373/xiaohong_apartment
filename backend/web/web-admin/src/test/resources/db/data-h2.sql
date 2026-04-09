-- 小红公寓claw H2 数据库测试数据初始化脚本

-- ==================== 系统管理数据 ====================

-- 系统岗位数据
MERGE INTO system_post (id, name, code, status, create_time, update_time, is_deleted) VALUES
(1, '超级管理员', 'SUPER_ADMIN', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, '管理员', 'ADMIN', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, '运营人员', 'OPERATOR', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(4, '客服人员', 'CUSTOMER_SERVICE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- 系统用户数据 (密码: admin - SHA256)
MERGE INTO system_user (id, username, password, name, type, phone, avatar_url, additional_info, post_id, status, create_time, update_time, is_deleted) VALUES
(1, 'admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '系统管理员', 0, '13800138000', 'https://example.com/avatar1.png', '超级管理员账号', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, 'operator', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '运营人员', 1, '13800138001', 'https://example.com/avatar2.png', '运营人员账号', 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- ==================== 区域数据 ====================

-- 省份数据
MERGE INTO province_info (id, name, create_time, update_time, is_deleted) VALUES
(1, '北京市', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, '上海市', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, '广东省', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(4, '浙江省', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(5, '江苏省', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- 城市数据
MERGE INTO city_info (id, name, province_id, create_time, update_time, is_deleted) VALUES
(1, '北京市', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, '上海市', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, '广州市', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(4, '深圳市', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(5, '杭州市', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(6, '南京市', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(7, '苏州市', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- 区县数据
MERGE INTO district_info (id, name, city_id, create_time, update_time, is_deleted) VALUES
(1, '朝阳区', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, '海淀区', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, '浦东新区', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(4, '徐汇区', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(5, '天河区', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(6, '越秀区', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(7, '福田区', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(8, '南山区', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(9, '西湖区', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(10, '上城区', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(11, '鼓楼区', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(12, '玄武区', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(13, '工业园区', 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(14, '姑苏区', 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- ==================== 用户信息数据 ====================

-- 用户信息 (密码: 123456 - SHA256)
MERGE INTO user_info (id, phone, password, status, nickname, avatar_url, create_time, update_time, is_deleted) VALUES
(1, '13800138001', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, '张三', 'https://example.com/user1.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, '13800138002', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, '李四', 'https://example.com/user2.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, '13800138003', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, '王五', 'https://example.com/user3.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(4, '13800138004', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 0, '赵六', 'https://example.com/user4.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- ==================== 公寓基础数据 ====================

-- 属性数据
MERGE INTO attr_key (id, name, create_time, update_time, is_deleted) VALUES
(1, '户型', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, '面积', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, '朝向', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(4, '楼层', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO attr_value (id, name, attr_key_id, create_time, update_time, is_deleted) VALUES
(1, '一室一厅', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, '两室一厅', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, '三室两厅', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(4, '50㎡以下', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(5, '50-80㎡', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(6, '80-100㎡', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(7, '朝南', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(8, '朝北', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(9, '朝东', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(10, '朝西', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(11, '低楼层', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(12, '中楼层', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(13, '高楼层', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- 杂费数据
MERGE INTO fee_key (id, name, create_time, update_time, is_deleted) VALUES
(1, '水费', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, '电费', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, '燃气费', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(4, '物业费', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(5, '网费', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO fee_value (id, name, unit, fee_key_id, create_time, update_time, is_deleted) VALUES
(1, '居民用水', '元/吨', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, '商业用水', '元/吨', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, '居民用电', '元/度', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(4, '商业用电', '元/度', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(5, '居民燃气', '元/立方', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(6, '标准物业', '元/㎡·月', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(7, '百兆宽带', '元/月', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- 支付方式
MERGE INTO payment_type (id, name, pay_month_count, additional_info, create_time, update_time, is_deleted) VALUES
(1, '月付', 1, '每月支付租金', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, '季付', 3, '每3个月支付租金', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, '半年付', 6, '每6个月支付租金', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(4, '年付', 12, '每年支付租金', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- 租期
MERGE INTO lease_term (id, month_count, unit, create_time, update_time, is_deleted) VALUES
(1, 3, '月', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, 6, '月', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, 12, '月', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(4, 24, '月', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- 配套设施
MERGE INTO facility_info (id, type, name, create_time, update_time, is_deleted) VALUES
(1, 1, '洗衣机', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, 1, '冰箱', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, 1, '空调', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(4, 1, '电视', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(5, 1, '热水器', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(6, 1, '宽带', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(7, 1, '沙发', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(8, 1, '床', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(9, 1, '衣柜', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(10, 1, '书桌', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(11, 2, '电梯', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(12, 2, '停车位', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(13, 2, '健身房', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(14, 2, '游泳池', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(15, 2, '花园', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- 标签
MERGE INTO label_info (id, type, name, create_time, update_time, is_deleted) VALUES
(1, 1, '近地铁', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, 1, '精装修', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, 1, '拎包入住', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(4, 1, '南北通透', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(5, 1, '采光好', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(6, 2, '押一付一', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(7, 2, '无中介费', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(8, 2, '随时看房', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- ==================== 公寓数据 ====================

-- 公寓信息
MERGE INTO apartment_info (id, name, introduction, district_id, district_name, city_id, city_name, province_id, province_name, address_detail, latitude, longitude, phone, is_release, avg_score, score_count, status, create_time, update_time, is_deleted) VALUES
(1, '小红公寓·阳光社区', '位于市中心，交通便利，周边配套设施齐全', 1, '朝阳区', 1, '北京市', 1, '北京市', '朝阳区建国路88号', '39.9042', '116.4074', '400-888-0001', 1, 4.80, 15, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, '小红公寓·绿地社区', '环境优美，绿化率高，适合居住', 3, '浦东新区', 2, '上海市', 2, '上海市', '浦东新区陆家嘴环路1000号', '31.2304', '121.4737', '400-888-0002', 1, 4.50, 8, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, '小红公寓·海景社区', '靠近海边，风景优美，空气清新', 7, '福田区', 4, '深圳市', 3, '广东省', '福田区福华路168号', '22.5431', '114.0579', '400-888-0003', 1, 4.20, 5, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- 房间信息
MERGE INTO room_info (id, apartment_id, room_number, rent, is_release, status, create_time, update_time, is_deleted) VALUES
(1, 1, '101', 3500.00, 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, 1, '102', 3800.00, 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, 1, '201', 4200.00, 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(4, 2, 'A101', 4500.00, 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(5, 2, 'A102', 4800.00, 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(6, 3, '1-101', 5000.00, 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- ==================== 预约看房数据 ====================

MERGE INTO view_appointment (id, user_id, apartment_id, appointment_time, appointment_status, create_time, update_time, is_deleted) VALUES
(1, 1, 1, TIMESTAMPADD('DAY', 1, CURRENT_TIMESTAMP), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, 2, 2, TIMESTAMPADD('DAY', 2, CURRENT_TIMESTAMP), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, 3, 1, TIMESTAMPADD('DAY', 3, CURRENT_TIMESTAMP), 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- ==================== 租约数据 ====================

MERGE INTO lease_agreement (id, user_id, apartment_id, room_id, lease_start_date, lease_end_date, rent, deposit, status, create_time, update_time, is_deleted) VALUES
(1, 1, 1, 1, DATEADD('MONTH', -1, CURRENT_DATE), DATEADD('MONTH', 11, CURRENT_DATE), 3500.00, 3500.00, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, 2, 2, 4, DATEADD('MONTH', -2, CURRENT_DATE), DATEADD('MONTH', 10, CURRENT_DATE), 4500.00, 4500.00, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- ==================== 公寓评分数据 ====================

MERGE INTO apartment_rating (id, user_id, apartment_id, environment_score, traffic_score, facility_score, service_score, value_score, overall_score, content, is_anonymous, status, create_time, update_time, is_deleted) VALUES
(1, 1, 1, 5.00, 5.00, 4.50, 5.00, 4.50, 4.80, '环境很好，交通便利，非常满意', 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, 2, 1, 4.50, 4.50, 5.00, 4.50, 4.50, 4.60, '设施齐全，服务周到', 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, 3, 1, 5.00, 5.00, 5.00, 5.00, 5.00, 5.00, '完美体验，强烈推荐', 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(4, 1, 2, 4.00, 4.50, 4.00, 4.50, 4.50, 4.30, '绿化很好，住着很舒服', 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(5, 2, 2, 4.50, 4.00, 4.50, 4.50, 5.00, 4.50, '性价比高，环境优美', 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(6, 3, 3, 4.00, 4.00, 4.00, 4.00, 4.00, 4.00, '海景不错，就是有点贵', 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- ==================== 后台操作日志数据 ====================

MERGE INTO system_operation_log (id, title, business_type, method, request_method, operator_type, oper_name, oper_url, oper_ip, status, oper_time, create_time, update_time, is_deleted) VALUES
(1, '用户登录', 7, 'com.xiaohong.lease.web.admin.controller.login.LoginController.login', 'POST', 1, 'admin', '/admin/login', '127.0.0.1', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(2, '保存或更新后台用户信息', 1, 'com.xiaohong.lease.web.admin.controller.system.SystemUserController.saveOrUpdate', 'POST', 1, 'admin', '/admin/system/user/saveOrUpdate', '127.0.0.1', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
(3, '删除角色', 3, 'com.xiaohong.lease.web.admin.controller.system.SystemRoleController.removeById', 'DELETE', 1, 'admin', '/admin/system/role/removeById', '127.0.0.1', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);
