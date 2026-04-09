package com.xiaohong.lease.web.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaohong.lease.model.entity.SystemRole;
import com.xiaohong.lease.web.admin.mapper.SystemRoleMapper;
import com.xiaohong.lease.web.admin.service.SystemRoleService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SystemRoleServiceImpl extends ServiceImpl<SystemRoleMapper, SystemRole> implements SystemRoleService {

    // 模拟数据模式 - 不依赖数据库
    private static final List<SystemRole> MOCK_ROLES = new ArrayList<>();
    
    static {
        SystemRole role1 = new SystemRole();
        role1.setId(1L);
        role1.setRoleName("超级管理员");
        role1.setRoleCode("super_admin");
        role1.setDescription("拥有所有权限");
        role1.setStatus(1);
        
        SystemRole role2 = new SystemRole();
        role2.setId(2L);
        role2.setRoleName("普通管理员");
        role2.setRoleCode("admin");
        role2.setDescription("拥有部分权限");
        role2.setStatus(1);
        
        MOCK_ROLES.add(role1);
        MOCK_ROLES.add(role2);
    }

    @Override
    public List<SystemRole> list() {
        // 如果数据库表不存在，返回模拟数据
        try {
            return super.list();
        } catch (Exception e) {
            return MOCK_ROLES;
        }
    }

    // 不重写page方法，使用父类的实现
    // 如果需要模拟分页数据，在Controller中处理

    @Override
    public void updateMenuListById(Long roleId, List<Long> menuIdList) {
        // TODO: 等数据库表创建后实现
        System.out.println("模拟：为角色 " + roleId + " 分配菜单: " + menuIdList);
    }
}
