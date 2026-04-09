package com.xiaohong.lease.web.admin.config;

import com.xiaohong.lease.model.entity.*;
import com.xiaohong.lease.model.enums.BaseStatus;
import com.xiaohong.lease.web.admin.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PostConstruct;
import java.util.Date;

/**
 * 测试数据初始化配置
 */
@TestConfiguration
@Profile("test")
public class TestDataInitializer {

    @Autowired
    private SystemUserMapper systemUserMapper;

    @PostConstruct
    public void init() {
        try {
            // 检查是否已有数据
            if (systemUserMapper.selectById(1L) == null) {
                // 创建测试用户
                SystemUser user = new SystemUser();
                user.setId(1L);
                user.setUsername("admin");
                // password = 'admin' SHA-256
                user.setPassword("8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92");
                user.setName("管理员");
                user.setStatus(BaseStatus.ENABLE);
                user.setCreateTime(new Date());
                user.setUpdateTime(new Date());
                user.setIsDeleted((byte) 0);
                systemUserMapper.insert(user);
                System.out.println("测试数据初始化完成");
            }
        } catch (Exception e) {
            System.out.println("测试数据初始化失败: " + e.getMessage());
        }
    }
}
