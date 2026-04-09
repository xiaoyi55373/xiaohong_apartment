package com.xiaohong.lease.web.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaohong.lease.model.entity.SystemMenu;
import com.xiaohong.lease.web.admin.mapper.SystemMenuMapper;
import com.xiaohong.lease.web.admin.service.SystemMenuService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SystemMenuServiceImpl extends ServiceImpl<SystemMenuMapper, SystemMenu> implements SystemMenuService {

    // 模拟数据
    private static final List<SystemMenu> MOCK_MENUS = new ArrayList<>();
    
    static {
        SystemMenu menu1 = new SystemMenu();
        menu1.setId(1L);
        menu1.setParentId(0L);
        menu1.setTitle("系统管理");
        menu1.setPath("/system");
        menu1.setComponent("Layout");
        menu1.setType(1);
        menu1.setIcon("el-icon-setting");
        menu1.setSort(100);
        menu1.setStatus(1);
        
        SystemMenu menu2 = new SystemMenu();
        menu2.setId(2L);
        menu2.setParentId(1L);
        menu2.setTitle("用户管理");
        menu2.setPath("/system/user");
        menu2.setComponent("system/user/index");
        menu2.setType(2);
        menu2.setIcon("el-icon-user");
        menu2.setSort(1);
        menu2.setStatus(1);
        
        SystemMenu menu3 = new SystemMenu();
        menu3.setId(3L);
        menu3.setParentId(1L);
        menu3.setTitle("角色管理");
        menu3.setPath("/system/role");
        menu3.setComponent("system/role/index");
        menu3.setType(2);
        menu3.setIcon("el-icon-s-custom");
        menu3.setSort(2);
        menu3.setStatus(1);
        
        MOCK_MENUS.add(menu1);
        MOCK_MENUS.add(menu2);
        MOCK_MENUS.add(menu3);
    }

    @Override
    public List<SystemMenu> list() {
        try {
            return super.list();
        } catch (Exception e) {
            return MOCK_MENUS;
        }
    }

    @Override
    public List<SystemMenu> listAsTree() {
        try {
            // TODO: 实现树形结构查询
            return super.list();
        } catch (Exception e) {
            return MOCK_MENUS;
        }
    }

    @Override
    public List<SystemMenu> listByRoleId(Long roleId) {
        try {
            // TODO: 根据角色ID查询菜单
            return super.list();
        } catch (Exception e) {
            return MOCK_MENUS;
        }
    }
}
