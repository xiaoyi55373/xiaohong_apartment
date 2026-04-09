package com.xiaohong.lease.web.admin.config;

import com.xiaohong.lease.model.entity.*;
import com.xiaohong.lease.model.enums.BaseStatus;
import com.xiaohong.lease.model.enums.SystemUserType;
import com.xiaohong.lease.model.enums.AppointmentStatus;
import com.xiaohong.lease.web.admin.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PostConstruct;
import java.util.Date;

/**
 * 测试数据初始化配置
 * 在测试开始前创建基础数据
 */
@TestConfiguration
@Profile("test")
public class TestDataConfig {

    @Autowired
    private SystemUserMapper systemUserMapper;
    
    @Autowired
    private SystemPostMapper systemPostMapper;
    
    @Autowired
    private ProvinceInfoMapper provinceInfoMapper;
    
    @Autowired
    private CityInfoMapper cityInfoMapper;
    
    @Autowired
    private DistrictInfoMapper districtInfoMapper;
    
    @Autowired
    private UserInfoMapper userInfoMapper;
    
    @Autowired
    private ViewAppointmentMapper viewAppointmentMapper;
    
    @Autowired
    private AttrKeyMapper attrKeyMapper;
    
    @Autowired
    private FeeKeyMapper feeKeyMapper;

    @PostConstruct
    public void init() {
        try {
            System.out.println("开始初始化测试数据...");
            
            // 初始化岗位数据
            initSystemPosts();
            
            // 初始化系统用户
            initSystemUsers();
            
            // 初始化区域数据
            initRegionData();
            
            // 初始化用户信息
            initUserInfo();
            
            // 初始化预约数据
            initAppointments();
            
            // 初始化属性数据
            initAttrData();
            
            // 初始化杂费数据
            initFeeData();
            
            System.out.println("测试数据初始化完成!");
        } catch (Exception e) {
            System.err.println("测试数据初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initSystemPosts() {
        if (systemPostMapper.selectById(1L) == null) {
            SystemPost post = new SystemPost();
            post.setId(1L);
            post.setName("超级管理员");
            post.setStatus(BaseStatus.ENABLE);
            post.setCreateTime(new Date());
            post.setUpdateTime(new Date());
            post.setIsDeleted((byte) 0);
            systemPostMapper.insert(post);
            System.out.println("  - 创建岗位: 超级管理员");
        }
    }
    
    private void initSystemUsers() {
        if (systemUserMapper.selectById(1L) == null) {
            SystemUser user = new SystemUser();
            user.setId(1L);
            user.setUsername("admin");
            user.setPassword("8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92");
            user.setName("系统管理员");
            user.setType(SystemUserType.ADMIN);
            user.setPhone("13800138000");
            user.setPostId(1L);
            user.setStatus(BaseStatus.ENABLE);
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
            user.setIsDeleted((byte) 0);
            systemUserMapper.insert(user);
            System.out.println("  - 创建用户: admin");
        }
    }
    
    private void initRegionData() {
        // 省份
        if (provinceInfoMapper.selectById(1) == null) {
            ProvinceInfo province = new ProvinceInfo();
            province.setId(1L);
            province.setName("北京市");
            provinceInfoMapper.insert(province);
            System.out.println("  - 创建省份: 北京市");
        }
        
        // 城市
        if (cityInfoMapper.selectById(1) == null) {
            CityInfo city = new CityInfo();
            city.setId(1L);
            city.setName("北京市");
            city.setProvinceId(1);
            cityInfoMapper.insert(city);
            System.out.println("  - 创建城市: 北京市");
        }
        
        // 区县
        if (districtInfoMapper.selectById(1) == null) {
            DistrictInfo district = new DistrictInfo();
            district.setId(1L);
            district.setName("朝阳区");
            district.setCityId(1);
            districtInfoMapper.insert(district);
            System.out.println("  - 创建区县: 朝阳区");
        }
    }
    
    private void initUserInfo() {
        if (userInfoMapper.selectById(1L) == null) {
            UserInfo user = new UserInfo();
            user.setId(1L);
            user.setPhone("13800138001");
            user.setPassword("8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92");
            user.setNickname("张三");
            user.setStatus(BaseStatus.ENABLE);
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
            user.setIsDeleted((byte) 0);
            userInfoMapper.insert(user);
            System.out.println("  - 创建用户: 张三");
        }
    }
    
    private void initAppointments() {
        if (viewAppointmentMapper.selectById(1L) == null) {
            ViewAppointment appointment = new ViewAppointment();
            appointment.setId(1L);
            appointment.setUserId(1L);
            appointment.setApartmentId(1L);
            appointment.setAppointmentTime(new Date());
            appointment.setAppointmentStatus(AppointmentStatus.WAITING);
            appointment.setCreateTime(new Date());
            appointment.setUpdateTime(new Date());
            appointment.setIsDeleted((byte) 0);
            viewAppointmentMapper.insert(appointment);
            System.out.println("  - 创建预约数据");
        }
    }
    
    private void initAttrData() {
        if (attrKeyMapper.selectById(1L) == null) {
            AttrKey attrKey = new AttrKey();
            attrKey.setId(1L);
            attrKey.setName("户型");
            attrKey.setCreateTime(new Date());
            attrKey.setUpdateTime(new Date());
            attrKey.setIsDeleted((byte) 0);
            attrKeyMapper.insert(attrKey);
            System.out.println("  - 创建属性: 户型");
        }
    }
    
    private void initFeeData() {
        if (feeKeyMapper.selectById(1L) == null) {
            FeeKey feeKey = new FeeKey();
            feeKey.setId(1L);
            feeKey.setName("水费");
            feeKey.setCreateTime(new Date());
            feeKey.setUpdateTime(new Date());
            feeKey.setIsDeleted((byte) 0);
            feeKeyMapper.insert(feeKey);
            System.out.println("  - 创建杂费: 水费");
        }
    }
}
